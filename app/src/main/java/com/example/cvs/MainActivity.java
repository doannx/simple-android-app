package com.example.cvs;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.cvs.dto.BrandDto;
import com.example.cvs.dto.FunctionDto;
import com.example.cvs.service.BusinessService;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NoticeDialogFragment.NoticeDialogListener {
    private String[] mPlanetTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;

    private int selectedCategoryId;
    private int selectedInfoKind;

    BusinessService service = new BusinessService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        service.context = this;

        List<FunctionDto> functions = this.getAllFunctions();
        Spinner spinCategory = (Spinner) findViewById(R.id.spinCategory);
        ArrayAdapter<FunctionDto> adapterCategory = new ArrayAdapter<FunctionDto>(this,
                android.R.layout.simple_spinner_dropdown_item, functions);
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinCategory.setAdapter(adapterCategory);
        spinCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                FunctionDto dto = (FunctionDto) adapterView.getSelectedItem();
                selectedCategoryId = dto.getId();
                buildOptions(getNumOfCategory(dto.getId()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        Spinner spinInfoKind = (Spinner) findViewById(R.id.spinner2);
        List<String> infoKind = Arrays.asList("Brand", "Generic");
        ArrayAdapter<String> adapterInfoKind = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, infoKind);
        spinInfoKind.setAdapter(adapterInfoKind);
        spinInfoKind.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedInfoKind = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

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
                        intent = new Intent(MainActivity.this, MainActivity.class);
                        break;
                    default:
                        intent = new Intent(MainActivity.this, AddFunctionActivity.class);
                        break;
                }
                startActivity(intent);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void check(View view) {
        final TableLayout tableLayout = (TableLayout) findViewById(R.id.tblOption);

        List<BrandDto> answers = this.getCorrectAnswers(this.selectedCategoryId);

        List<String> corrects = new ArrayList<String>();
        for (BrandDto dto : answers) {
            if (this.selectedInfoKind == 0) {
                corrects.add(dto.getBrand());
            } else {
                corrects.add(dto.getGeneric());
            }
        }

        int correct = 0;
        String incorrect = "";
        for (int i = 0; i < tableLayout.getChildCount(); i++) {
            TableRow tableRow = (TableRow) tableLayout.getChildAt(i);
            EditText text = (EditText) tableRow.getChildAt(0);
            if (corrects.contains(text.getText().toString())) {
                text.setBackgroundColor(Color.GREEN);
                correct++;
                corrects.remove(text.getText().toString());
            } else {
                text.setBackgroundColor(Color.RED);
            }
            text.setTextColor(Color.WHITE);
        }

        for (String s : corrects) {
            incorrect += "\n" + s;
        }
        if (correct == tableLayout.getChildCount()) {
            this.showNoticeDialog("Great, sweetie ^^");
        } else {
            this.showNoticeDialog(String.format("Sweetie, you did wrong at %d following item(s):\n %s", (tableLayout.getChildCount() - correct), incorrect));
        }

        return;
    }

    private void buildOptions(int n) {
        final TableLayout tableLayout = (TableLayout) findViewById(R.id.tblOption);
        tableLayout.removeAllViews();

        for (int r = 0; r < n; r++) {
            TableRow tableRow = new TableRow(this);
            EditText text = new EditText(this);
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT);
            layoutParams.weight = 1;
            layoutParams.topMargin = 10;
            text.setLayoutParams(layoutParams);
            text.setMaxLines(1);
            text.setImeOptions(1);

            tableRow.addView(text);
            tableLayout.addView(tableRow);
        }
    }

    private int getNumOfCategory(int functionId) {
        int count = 0;
        try {
            count = service.countByFunctionId(functionId);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return count;
    }

    private List<BrandDto> getCorrectAnswers(int functionId) {
        List<BrandDto> res = new ArrayList<BrandDto>();
        try {
            res = service.loadBrandsByFunctionId(functionId);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return res;
    }

    private List<FunctionDto> getAllFunctions() {
        List<FunctionDto> dtos = null;
        try {
            dtos = service.loadFunctionList();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return dtos;
    }

    private void showNoticeDialog(String content) {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new NoticeDialogFragment();
        Bundle bundle = new Bundle();

        bundle.putString("content", content);

        dialog.setArguments(bundle);
        dialog.show(this.getFragmentManager(), "CVS");
    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogFragment.NoticeDialogListener interface
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // User touched the dialog's positive button
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        onDialogPositiveClick(dialog);
    }
}