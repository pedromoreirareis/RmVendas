package com.pedromoreirareisgmail.rmvendas.activitys;

import android.app.DatePickerDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pedromoreirareisgmail.rmvendas.Fire.FireUtils;
import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.Calculus;
import com.pedromoreirareisgmail.rmvendas.Utils.Messages;
import com.pedromoreirareisgmail.rmvendas.Utils.PrefsUser;
import com.pedromoreirareisgmail.rmvendas.Utils.TimeDate;
import com.pedromoreirareisgmail.rmvendas.adapters.MainAdapter;
import com.pedromoreirareisgmail.rmvendas.constant.ConstIntents;
import com.pedromoreirareisgmail.rmvendas.constant.ConstLoader;
import com.pedromoreirareisgmail.rmvendas.db.Contract;
import com.pedromoreirareisgmail.rmvendas.db.Contract.EntrySeel;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LoaderManager.LoaderCallbacks<Cursor>,
        ListView.OnItemLongClickListener,
        ListView.OnItemClickListener,
        SearchView.OnQueryTextListener,
        FloatingActionButton.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private GoogleApiClient mGoogleApiClient;

    private Toolbar mToolbar;
    private FloatingActionButton mFab;
    private DrawerLayout mDrawer;
    private NavigationView mNavigationview;
    private TextView mTvEmpty;
    private ImageView mIvEmpty;
    private View mEmptyView;
    private ListView mListview;

    private Context mContext;
    private MainAdapter mAdapter;

    private String mSearchDateDB = null;
    private String mSearchDB = "";
    private String mUserName = "";

    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instancia FirebaseAuth e verifica se usuario esta logado
        initFirebase();
        initGoogleApiClient();

        initViews();
        emptyLayout();

        // Cria o ToolBar
        setSupportActionBar(mToolbar);

        // Cria o fab - botão flutuante
        mFab.setOnClickListener(this);

        /* Cria o menu de gaveta - Menu lateral
         * Indica que o botão toggle sera adcionado ao menu Drawer
         * e seu estado estara ssicronizado  ao menu drawer*/
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                mDrawer,
                mToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );

        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        /* Parte fisica do Drawer, onde realemente fica o itens do menu
         * O Drawer é o ViewGroup e NavigationView é uma view do Drawer*/
        mNavigationview.setNavigationItemSelectedListener(this);

        mUserName = mUser.getDisplayName();

        initListenerAndObject();
        initTitleDate();


        // Inicia Pesquisa no banco de dados
        getLoaderManager().initLoader(ConstLoader.LOADER_MAIN, null, this);
    }

    private void initFirebase() {

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        if (mUser == null) { // Não esta logado

            startActivity(new Intent(MainActivity.this, SignInActivity.class));
            finish();
            return;
        }
    }

    private void initGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
    }

    private void initViews() {

        // Referencia os itens Navegação Menu lateral
        mToolbar = findViewById(R.id.toolbar);
        mFab = findViewById(R.id.fab_add);
        mDrawer = findViewById(R.id.drawer_layout);
        mNavigationview = findViewById(R.id.nav_view);

        // Referencia os itens do layout
        mTvEmpty = findViewById(R.id.tv_empty_view_big);
        mIvEmpty = findViewById(R.id.iv_empty_view_big);
        mListview = findViewById(R.id.lv_list);
        mEmptyView = findViewById(R.id.empty_view_big);
    }

    private void emptyLayout() {

        // EmptyView sera acionado se não houver nenhum registro no listview
        mTvEmpty.setText(R.string.text_main_empty);
        mIvEmpty.setImageResource(R.drawable.ic_woman_small);
        mIvEmpty.setContentDescription(getString(R.string.descr_main_empty));
        mListview.setEmptyView(mEmptyView);
    }

    private void initListenerAndObject() {

        // Contexto da Activity
        mContext = MainActivity.this;

        // Cria o adapter e o ListView
        mAdapter = new MainAdapter(mContext);
        mListview.setAdapter(mAdapter);
        mListview.setDivider(null);

        // Click longo no Listview e click simples
        mListview.setOnItemLongClickListener(this);
        mListview.setOnItemClickListener(this);
    }

    private void initTitleDate() {

        //  Obtem a data calendário do Dialog
        getCalendarDate();

        // Adiciona data como Titulo da Activity
        setTitle(TimeDate.getDateTitleBr());

        // Obtem data para iniciar pesquisa no banco de dados
        mSearchDateDB = TimeDate.formatDateSearch(TimeDate.getDateTime());
    }

    @Override
    protected void onStart() {
        super.onStart();

        getSettings();
    }

    private void getSettings() {

        String companyName = PrefsUser.getCompanyName(mContext);

        TextView tvUserName = mNavigationview.getHeaderView(0).findViewById(R.id.tv_nav_user_name);
        TextView tvCompanyName = mNavigationview.getHeaderView(0).findViewById(R.id.tv_nav_company_name);

        if (!mUserName.isEmpty()) {

            tvUserName.setVisibility(View.VISIBLE);
            tvUserName.setText(mUserName);
        }

        if (!companyName.isEmpty()) {

            tvCompanyName.setVisibility(View.VISIBLE);
            tvCompanyName.setText(String.format(getString(R.string.text_company_name), companyName));
        }
    }

    @Override
    public void onBackPressed() {

        // Referencia o o Drawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        // Se Drawer Estiver aberto então fecha
        if (drawer.isDrawerOpen(GravityCompat.START)) {

            drawer.closeDrawer(GravityCompat.START);

        } else {

            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_search_date, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search_menu_search_date);

        SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            // Item Calendario - Abre para fazer uma pesquisa por data no BD vendas
            case R.id.action_date_menu_search_date:
                Messages.dialogCalendar(
                        mContext,
                        mDateSetListener
                );
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            // Click no menu Entrada
            case R.id.action_add_money_menu_drawer:
                startActivity(new Intent(mContext, ListAddMoneytActivity.class));
                break;

            // Click no menu Retirada
            case R.id.action_remove_money_menu_drawer:
                startActivity(new Intent(mContext, ListRemoveMoneyActivity.class));
                break;

            // Click no menu Saldo Inicial
            case R.id.action_opening_menu_drawer:
                startActivity(new Intent(mContext, ListOpeningActivity.class));
                break;

            // Click no menu Fechamento
            case R.id.action_closed_menu_drawer:
                startActivity(new Intent(mContext, ClosedActivity.class));
                break;

            // Click no menu Produtos
            case R.id.action_products_menu_drawer:
                startActivity(new Intent(mContext, ListProductActivity.class));
                break;

            // Click no menu Clientes
            case R.id.action_clients_menu_drawer:
                startActivity(new Intent(mContext, ListClientActivity.class));
                break;

            // Click no menu Configurações
            case R.id.action_settings_menu_drawer:
                startActivity(new Intent(mContext, SettingsActivity.class));
                break;

            // Sair da conta Google
            case R.id.action_sign_out_menu_drawer:
                mAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                mUser = null;
                startActivity(new Intent(mContext, SignInActivity.class));
                finish();

        }

        // Apos o click o Drawer é fechado
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {


        // Define quais colunas retornaram dados
        String[] projection = new String[]{
                EntrySeel._ID,
                EntrySeel.COLUMN_NAME,
                EntrySeel.COLUMN_QUANTITY,
                EntrySeel.COLUMN_TIMESTAMP,
                EntrySeel.COLUMN_DISCOUNT_VALUE,
                EntrySeel.COLUMN_ADD_VALUE,
                EntrySeel.COLUMN_FORWARD_VALUE,
                EntrySeel.COLUMN_CLIENT_ID,
                EntrySeel.COLUMN_PRICE,
                EntrySeel.COLUMN_RECEIVE_ID
        };

        String selection;
        String[] selectionArgs;
        String sortOrder;

        /* Dados para pesquisa
         * mSearchDB - dado digitados no edit do SearchView
         * mSearchDateDB - Data do dia ou data selecionada no item de menu calendario
         *
         * Verifica se há pelo menos 1 caractere em mSearchDB, se tiver, a pesquisa sera feita
         * utilizando mSearchDB e mSearchDateDB. Sera retornado dados de uma data especifica e
         * que cotenham os caracteres do mSearchDB no nome do produto
         *
         * Se mSearchDB estiver vazio, trara dados apenas da data que estiver em mSearchDateDB */
        if (mSearchDB.length() > 0) {

            // Paramentro da pesquisa - Pesquisa por data e nome do produto
            selection = EntrySeel.COLUMN_TIMESTAMP + " LIKE ?  AND " + EntrySeel.COLUMN_NAME + " LIKE ?";

            // Argumentos
            selectionArgs = new String[]{mSearchDateDB + "%", "%" + mSearchDB + "%"};

            // Ordem de retorno sera por data, mas da ultima para primeira
            sortOrder = EntrySeel.COLUMN_TIMESTAMP + " DESC";

        } else {

            selection = EntrySeel.COLUMN_TIMESTAMP + " LIKE ?";
            selectionArgs = new String[]{mSearchDateDB + "%"};
            sortOrder = EntrySeel.COLUMN_TIMESTAMP + " DESC";
        }

        return new CursorLoader(
                mContext,
                EntrySeel.CONTENT_URI_SELL,
                projection,
                selection,
                selectionArgs,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mAdapter.swapCursor(null);
    }

    /* Obtem a data que sera utilizada para pesquisa no banco de dados. Sera formatada
     * no formato usado no Barsil e sera mostrada no titulo da Activity*/
    private void getCalendarDate() {

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                mSearchDateDB = TimeDate.getDateSearchDB(year, month, day);

                setTitle(TimeDate.getDateTitleBr(year, month, day));

                getLoaderManager().restartLoader(
                        ConstLoader.LOADER_MAIN,
                        null,
                        MainActivity.this
                );
            }
        };
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        mSearchDB = newText;

        getLoaderManager().restartLoader(ConstLoader.LOADER_MAIN, null, MainActivity.this);

        return true;
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.fab_add) {

            Intent intentAddSell = new Intent(mContext, ListProductSaleActivity.class);
            startActivity(intentAddSell);
        }
    }

    /* Envia dados para calculo do troco do cliente */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Cursor cursor = mAdapter.getCursor();

        double sightSaleValue = Calculus.CalcularValorAVistaDouble(
                cursor.getInt(cursor.getColumnIndex(EntrySeel.COLUMN_QUANTITY)),
                cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_PRICE)),
                cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_ADD_VALUE)),
                cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_DISCOUNT_VALUE)),
                cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_FORWARD_VALUE))
        );

        if (sightSaleValue > 0) {

            Intent intentChange = new Intent(mContext, MoneyBackActivity.class);

            Bundle bundle = new Bundle();
            bundle.putDouble(ConstIntents.INTENT_MONEY_BACK, sightSaleValue);

            intentChange.putExtras(bundle);
            startActivity(intentChange);
        }
    }

    /* Abre dialog para escolha de edição ou exclusão */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        Cursor cursor = mAdapter.getCursor();

        Long receiveId = cursor.getLong(cursor.getColumnIndex(EntrySeel.COLUMN_RECEIVE_ID));

        Uri uriSell = ContentUris.withAppendedId(EntrySeel.CONTENT_URI_SELL, id);
        Uri uriReceive = ContentUris.withAppendedId(Contract.EntryReceive.CONTENT_URI_RECEIVE, receiveId);

        String messageDelete;


        if (receiveId > 0) {

            messageDelete = String.format(
                    getString(R.string.text_delete_item_sale_receive),
                    cursor.getString(cursor.getColumnIndex(EntrySeel.COLUMN_QUANTITY)),
                    cursor.getString(cursor.getColumnIndex(EntrySeel.COLUMN_NAME)));

            Messages.editOurDeleteSell(
                    mContext,
                    view,
                    uriSell,
                    uriReceive,
                    messageDelete
            );

        } else {

            messageDelete = String.format(
                    getString(R.string.text_delete_item_sale),
                    cursor.getString(cursor.getColumnIndex(EntrySeel.COLUMN_QUANTITY)),
                    cursor.getString(cursor.getColumnIndex(EntrySeel.COLUMN_NAME)));

            Messages.editOurDelete(
                    mContext,
                    SellActivity.class,
                    uriSell,
                    messageDelete
            );
        }

        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStop() {
        super.onStop();

        FireUtils.firebaseSetNull();
    }
}
