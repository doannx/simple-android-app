package com.example.cvs;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.cvs.dto.BrandDto;
import com.example.cvs.dto.FunctionDto;
import com.example.cvs.service.BusinessService;

import java.util.ArrayList;
import java.util.List;

public class AddFunctionActivity extends AppCompatActivity {

    private String[] mPlanetTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;

    BusinessService service = new BusinessService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_function);

        service.context = this;

        try {
            loadData4Spinner();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        mPlanetTitles = getResources().getStringArray(R.array.menu);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mPlanetTitles));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent;
                switch (i) {
                    case 0:
                        intent = new Intent(AddFunctionActivity.this, MainActivity.class);
                        break;
                    default:
                        intent = new Intent(AddFunctionActivity.this, AddFunctionActivity.class);
                        break;
                }
                startActivity(intent);
            }
        });

    }

    private void loadData4Spinner() throws PackageManager.NameNotFoundException {
        List<String> functions = this.getAllFunctions();
        Spinner spinCategory = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<FunctionDto> adapterCategory = new ArrayAdapter<FunctionDto>(this,
                android.R.layout.simple_spinner_dropdown_item, service.loadFunctionList());
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinCategory.setAdapter(adapterCategory);
    }

    private List<String> getAllFunctions() {
        List<String> res = new ArrayList<String>();
        List<FunctionDto> dtos = null;
        try {
            dtos = service.loadFunctionList();
            for (FunctionDto dto : dtos) {
                res.add(dto.getDescription());
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return res;
    }

    public void saveBrand(View view) throws PackageManager.NameNotFoundException {
        EditText txtBrand = (EditText) findViewById(R.id.txtBrand);
        EditText txtGeneric = (EditText) findViewById(R.id.txtGeneric);
        Spinner spinCategory = (Spinner) findViewById(R.id.spinner);
        FunctionDto dto = (FunctionDto) spinCategory.getSelectedItem();
        service.saveBrand(new BrandDto(txtBrand.getText().toString(), txtGeneric.getText().toString(), dto.getId()));
    }

    public void addNewFunction(View view) throws PackageManager.NameNotFoundException {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(view.getContext());

        View promptView = layoutInflater.inflate(R.layout.dialog_add_function, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());

        // set prompts.xml to be the layout file of the alertdialog builder
        alertDialogBuilder.setView(promptView);

        final EditText input = (EditText) promptView.findViewById(R.id.userInput);

        // setup a dialog window
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // get user input and set it to result
                        try {
                            service.saveFunction(new FunctionDto(input.getText().toString()));
                            loadData4Spinner();
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
    }
}
