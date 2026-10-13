package com.timmalone.rocketcodetest;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MainActivity extends Activity {
    private TextView display;
    private String current = "0";
    private BigDecimal stored = null;
    private String pendingOp = null;
    private boolean resetOnDigit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 48, 24, 24);
        root.setBackgroundColor(0xff0f1115);

        display = new TextView(this);
        display.setText(current);
        display.setTextColor(0xfff4f4f5);
        display.setTextSize(44);
        display.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        display.setSingleLine(true);
        root.addView(display, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1.2f));

        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(4);
        String[] keys = {"C", "±", "%", "÷", "7", "8", "9", "×", "4", "5", "6", "-", "1", "2", "3", "+", "0", ".", "="};
        for (String key : keys) {
            Button button = new Button(this);
            button.setText(key);
            button.setTextSize(24);
            button.setTextColor(0xfff4f4f5);
            button.setAllCaps(false);
            button.setBackgroundColor(isOperator(key) ? 0xff10a37f : 0xff2a2f3a);
            button.setOnClickListener(v -> press(((Button) v).getText().toString()));
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, key.equals("0") ? 2 : 1, 1f);
            params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(6, 6, 6, 6);
            grid.addView(button, params);
        }
        root.addView(grid, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 4f));
        setContentView(root);
    }

    private void press(String key) {
        if ("0123456789".contains(key)) appendDigit(key);
        else if (".".equals(key)) appendDecimal();
        else if ("C".equals(key)) clear();
        else if ("±".equals(key)) toggleSign();
        else if ("%".equals(key)) current = decimal().divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
        else if ("=".equals(key)) resolve();
        else if (isOperator(key)) chooseOperator(key);
        display.setText(current);
    }

    private void appendDigit(String digit) { if (resetOnDigit || "0".equals(current)) { current = digit; resetOnDigit = false; } else current += digit; }
    private void appendDecimal() { if (resetOnDigit) { current = "0"; resetOnDigit = false; } if (!current.contains(".")) current += "."; }
    private void clear() { current = "0"; stored = null; pendingOp = null; resetOnDigit = false; }
    private void toggleSign() { current = current.startsWith("-") ? current.substring(1) : "-" + current; if ("-0".equals(current)) current = "0"; }
    private void chooseOperator(String op) { if (stored != null && pendingOp != null && !resetOnDigit) resolve(); else stored = decimal(); pendingOp = op; resetOnDigit = true; }

    private void resolve() {
        if (stored == null || pendingOp == null) return;
        BigDecimal value = decimal();
        switch (pendingOp) {
            case "+": stored = stored.add(value); break;
            case "-": stored = stored.subtract(value); break;
            case "×": stored = stored.multiply(value); break;
            case "÷": stored = value.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : stored.divide(value, 10, RoundingMode.HALF_UP); break;
        }
        current = stored.stripTrailingZeros().toPlainString();
        pendingOp = null;
        resetOnDigit = true;
    }

    private BigDecimal decimal() { return new BigDecimal(current); }
    private boolean isOperator(String key) { return "+-×÷".contains(key); }
}
