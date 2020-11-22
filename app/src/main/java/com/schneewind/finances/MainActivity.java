package com.schneewind.finances;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemLongClickListener {

    private ListView EntryListView;
    private Button AddEntryButton;
    private EditText Amount;
    private AutoCompleteTextView Name;
    private TextView Balance;

    private ArrayList<String> entries_DataSet;
    private ArrayList<Transaction> transactions;

    private ArrayList<View> entryViews;
    CustomAdapter customAdapter = new CustomAdapter();


    String[] monthstring;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //UI
        monthstring = new String[]{getString(R.string.m_january),getString(R.string.m_february),getString(R.string.m_march),getString(R.string.m_april),getString(R.string.m_may),getString(R.string.m_june),getString(R.string.m_july),getString(R.string.m_august),getString(R.string.m_september),getString(R.string.m_october),getString(R.string.m_november),getString(R.string.m_december)};

        EntryListView = findViewById(R.id.Entries);
        AddEntryButton = findViewById(R.id.AddEntry);
        AddEntryButton.setOnClickListener(this);
        Amount = findViewById(R.id.amount_input);

        Name = findViewById(R.id.name_input);
        Name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE) AddEntryButton.callOnClick();
                return false;
            }
        });
        Name.setAdapter(new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,new String[]{
                getString(R.string.p_food), getString(R.string.p_transportation), getString(R.string.p_phone), "Google Play", "Steam", getString(R.string.p_clothes), getString(R.string.p_hairdresser)
        }));

        EntryListView.setOnItemLongClickListener(this);
        Balance = findViewById(R.id.balance);


        //List
        entries_DataSet = FileHelper.readDataFromDefaultFile(this);
        entryViews = new ArrayList<>();
        transactions = new ArrayList<>();
        getTransactions();

        customAdapter = new CustomAdapter();
        EntryListView.setAdapter(customAdapter);

        for(int i = 0;i < entries_DataSet.size(); i++)
        {
            entryViews.set(i,customAdapter.getView(i,null,null));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.reset_button) {
            final Context context = this;
            new AlertDialog.Builder(this)
                    .setTitle(R.string.w_reset)
                    .setMessage(R.string.msg_action_irreversible)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            entries_DataSet.clear();
                            FileHelper.writeDataToDefaultFile(entries_DataSet,context);
                            getTransactions();
                            customAdapter.notifyDataSetChanged();
                        }})
                    .setNegativeButton(android.R.string.no, null).show();
            return true;
        }

        if(id == R.id.export_button){
            FileHelper.writeDataToExternalFile(entries_DataSet, MainActivity.this);
            Toast.makeText(this, R.string.msg_feature_in_development,Toast.LENGTH_SHORT).show();
        }

        if(id == R.id.import_button){
            Toast.makeText(this, R.string.msg_feature_in_development,Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.AddEntry:
                String amount = Amount.getText().toString();
                String name = Name.getText().toString();

                if(amount == "") return;

                //Close Keyboard
                View v = getCurrentFocus();
                if(v != null){
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(),0);
                }

                //String modifications
                if(!amount.contains(".")) amount = amount.concat(".00");
                while (amount.substring(amount.length()-2).contains(".")){
                    amount = amount.concat("0");
                }
                while (amount.toCharArray()[0] == '0') {
                    amount = amount.substring(1);
                }
                if(name.contains(" : ")) name = name.replace(" : ","");

                entries_DataSet.add(
                        amount + " : " +
                        Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "." +
                        Calendar.getInstance().get(Calendar.MONTH)+ "." +
                        Calendar.getInstance().get(Calendar.YEAR) + " : " +
                        Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + ":" +
                        Calendar.getInstance().get(Calendar.MINUTE) + " : " +
                        name
                );
                Amount.setText("");
                Name.setText("");

                DataSetChanged();
                EntryListView.smoothScrollToPosition(entries_DataSet.size());
                break;
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.msg_delete)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        entries_DataSet.remove(i);
                        DataSetChanged();
                    }})
                .setNegativeButton(android.R.string.no, null).show();
        return false;
    }

    private void getTransactions () {
        String[] t = Arrays.copyOf(entries_DataSet.toArray(), entries_DataSet.toArray().length, String[].class);

        transactions.clear();
        double balance = 0;
        for(int j = 0; j < t.length;j++){
            String[] s = t[j].split(" : ");

            double height = Double.valueOf(s[0]);
            balance+= height;

            String date = s[1];
            String time = s[2];
            String name = null; if(s.length > 3) name = s[3];
            transactions.add(new Transaction(height,date,time,name));
        }

        //Displayed balance
        String b = String.valueOf(balance);
        while(!b.substring(b.length() - 3).contains(".")){
            b = b.substring(0,b.length()-1);
        }
        if(b.substring(b.length() - 2).contains(".")) b = b.concat("0");
        b = b.replace(".",",");
        Balance.setText(getString(R.string.t_sum) + b + "€");
    }

    private void DataSetChanged(){
        FileHelper.writeDataToDefaultFile(entries_DataSet, this);
        entryViews.clear();
        getTransactions();
        customAdapter.notifyDataSetChanged();
        for(int i = 0;i < entries_DataSet.size(); i++)
        {
            entryViews.set(i,customAdapter.getView(i,null,null));
        }

    }


    class CustomAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return entries_DataSet.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            try {
                return entryViews.get(i);
            } catch (Exception e){
                view = getLayoutInflater().inflate(R.layout.list_item, null);
            }

            Transaction t = transactions.get(i);

            ConstraintLayout date_divider_layout = view.findViewById(R.id.date_divider);
            TextView transaction_date_textview = view.findViewById(R.id.transaction_date);
            ConstraintLayout month_divider_layout = view.findViewById(R.id.month_divider);
            TextView month_divider_name = view.findViewById(R.id.month_divider_name);
            TextView month_divider_balance = view.findViewById(R.id.month_divider_balance);

            ImageView transaction_icon = view.findViewById(R.id.transaction_icon);
            TextView transaction_height_textview =  view.findViewById(R.id.transaction_amount);
            TextView transaction_time_textview = view.findViewById(R.id.transaction_time);
            TextView transaction_name_textview = view.findViewById(R.id.transaction_name);

            if(t.height > 0) transaction_icon.setImageResource(R.drawable.arrow_icon_up_green);
            if(t.height < 0) transaction_icon.setImageResource(R.drawable.arrow_icon_down_red);

            String transaction_height = t.displayedHeight();
            String transaction_date = t.displayedDate();
            String transaction_time = t.displayedTime();
            String transaction_name = t.name;

            //Applying Values
            transaction_height_textview.setText(transaction_height);
            transaction_time_textview.setText(transaction_time);
            transaction_name_textview.setText(transaction_name);

            transaction_name_textview.setSelected(true);

            //New month
            if(i == 0 || transactions.get(i - 1).getMonth() != t.getMonth()){

                String month_name = monthstring[transactions.get(i).getMonth()];
                month_divider_name.setText(month_name);

                double b = 0;
                for(int j = 0;j < i; j++){
                    b += transactions.get(j).height;
                }
                month_divider_balance.setText(t.displayedHeight(b));
            }
            else{
                month_divider_layout.setVisibility(View.GONE);
            }

            //Different date than previous entry
            if (i == 0 || !transactions.get(i - 1).displayedDate().equals(transaction_date)) {
                String[] d = transaction_date.split("\\.");
                transaction_date_textview.setText(d[0] + ". " + monthstring[transactions.get(i).getMonth()] + " " + d[2]);
            }
            else{
                date_divider_layout.setVisibility(View.GONE);
            }


            try {
                entryViews.add(i,view);
            } catch(Exception e){
                entryViews.set(i,view);
            }

            view.findViewById(R.id.transaction_amount).setSelected(true);
            return view;
        }
    }
}

