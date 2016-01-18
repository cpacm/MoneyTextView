package net.cpacm.moneyview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import net.cpacm.library.MoneyTextView;

public class MainActivity extends AppCompatActivity {

    private MoneyTextView moneyTv;
    private EditText moneyEt;
    private Button moneyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        moneyEt = (EditText) findViewById(R.id.money_et);
        moneyBtn = (Button) findViewById(R.id.money_btn);
        moneyTv = (MoneyTextView) findViewById(R.id.money_tv);
        moneyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moneyTv.setMoneyText(moneyEt.getText().toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.all:
                moneyTv.setMoneyMode(MoneyTextView.MoneyMode.ALL);
                break;
            case R.id.digit:
                moneyTv.setMoneyMode(MoneyTextView.MoneyMode.DIGIT);
                break;
            case R.id.disable:
                moneyTv.setMoneyFormat(MoneyTextView.MoneyFormat.FORMAT_DISABLE);
                break;
            case R.id.integer:
                moneyTv.setMoneyFormat(MoneyTextView.MoneyFormat.FORMAT_INTEGER);
                break;
            case R.id.floats:
                moneyTv.setMoneyFormat(MoneyTextView.MoneyFormat.FORMAT_FLOAT);
                break;
            case R.id.color:
                if (item.isChecked()) {
                    item.setChecked(false);
                    moneyTv.setMoneyColor(moneyTv.getCurrentTextColor());
                } else {
                    item.setChecked(true);
                    moneyTv.setMoneyColor(getResources().getColor(R.color.red));
                }
                break;
            case R.id.symbol:
                if (item.isChecked()) {
                    item.setChecked(false);
                    moneyTv.setSymbol("");
                } else {
                    item.setChecked(true);
                    moneyTv.setSymbol("￥");
                }
                break;
            case R.id.money_rate:
                moneyTv.setMoneyRate(1.2f);
                break;
            case R.id.symbol_rate:
                moneyTv.setSymbolRate(0.8f);
                break;
            case R.id.decimal_rate:
                moneyTv.setDecimalRate(0.6f);
                break;
            case R.id.clear_rate:
                moneyTv.setMoneyRate(1f);
                moneyTv.setSymbolRate(1f);
                moneyTv.setDecimalRate(1f);
                break;
            case R.id.font:
                if (item.isChecked()) {
                    item.setChecked(false);
                    moneyTv.setMoneyFont("");
                } else {
                    item.setChecked(true);
                    moneyTv.setMoneyFont("fonts/AkzidenzGrotConBQ-Regular.otf");
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
