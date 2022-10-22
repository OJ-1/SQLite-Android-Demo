package com.ojs.sqlitedemo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText customerName_et, customerAge_et;
    private Button add_bt, viewAll_bt;
    private Switch activeCustomer_sw;
    private ListView customerList_lv;

    private SQLiteDatabase myDatabase;

    private DAO dao;
    private ArrayAdapter customerArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        customerName_et = findViewById(R.id.name_et);
        customerAge_et = findViewById(R.id.age_et);
        add_bt = findViewById(R.id.add_bt);
        viewAll_bt = findViewById(R.id.viewAll_bt);
        activeCustomer_sw = findViewById(R.id.activeCustomer_sw);
        customerList_lv = findViewById(R.id.customerList_lv);

        dao = new DAO(MainActivity.this);
        updateListView();

        add_bt.setOnClickListener(v -> {
            CustomerModel customerModel;
            try {
                customerModel = new CustomerModel(
                        -1,
                        customerName_et.getText().toString(),
                        Integer.parseInt(customerAge_et.getText().toString()),
                        activeCustomer_sw.isChecked()
                );
                Toast.makeText(MainActivity.this, "Customer Added: " + customerModel.toString(), Toast.LENGTH_SHORT).show();
            } catch (Exception e){
                customerModel = new CustomerModel(-1, "Error", 0, false);
                Toast.makeText(MainActivity.this, "Error creating customer", Toast.LENGTH_SHORT).show();
            }

            boolean success = dao.addOne(customerModel);
            updateListView();
            Toast.makeText(MainActivity.this, "Add One Success = " + success, Toast.LENGTH_SHORT).show();
        });

        viewAll_bt.setOnClickListener(v -> {
            updateListView();
        });


        customerList_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setTitle("Delete Customer?");
                alertDialogBuilder
                        .setMessage("Click yes to delete this customer")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                CustomerModel clickedCustomer = (CustomerModel) parent.getItemAtPosition(position);
                                dao.deleteOne(clickedCustomer);
                                updateListView();
                                Toast.makeText(MainActivity.this, "Customer deleted", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

    }

    private void updateListView(){
        customerArrayAdapter = new ArrayAdapter<CustomerModel>(MainActivity.this, android.R.layout.simple_list_item_1, dao.getAll());
        customerList_lv.setAdapter(customerArrayAdapter);
    }

    //===
}