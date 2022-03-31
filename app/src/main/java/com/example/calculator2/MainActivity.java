package com.example.calculator2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private static final float  DEFAULT_RESULT              = 0.0f;

    private EditText    edNum1,
                        edNum2;
    private TextView    txtResult;
    private Button      btAdd,
                        btSub,
                        btMul,
                        btDiv,
                        btClear;
    private SeekBar     seekBar;
    private String      resultForamt = "2";
    private float       lastResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edNum1 =        findViewById(R.id.edOperand1);
        edNum2 =        findViewById(R.id.edOperand2);
        edNum1.addTextChangedListener(new ValidOperandTextWatcher());//this add listener to the editText
        edNum2.addTextChangedListener(new ValidOperandTextWatcher());//this add listener to the editText
        txtResult =     findViewById(R.id.txtRes);
        btAdd =         findViewById(R.id.btAdd);
        btSub =         findViewById(R.id.btSub);
        btMul =         findViewById(R.id.btMul);
        btDiv =         findViewById(R.id.btDiv);
        btClear =       findViewById(R.id.btClear);
        btClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearEditTexts();
                clearResultField();
                //setResultTextView(DEFAULT_RESULT);
            }
        });



        /**************************************************************/
        /*************************** Inflate **************************/
        /********************* The manually method *******************/

        ViewGroup mainLayout = (ViewGroup)findViewById(R.id.main_layout);  //get reference to the MAIN layout container the ROOT
        LinearLayout linearLayout=findViewById(R.id.linearLayout); //get reference to the secondary container that hold all components (button/edittext and etc).

        //get reference to the seek bar component and
        View child = (LinearLayout) getLayoutInflater().inflate(R.layout.seek_bar_sub_layout, mainLayout, false); //mainLayout is the parent(ROOT)
        //*** If attachToRoot is true, it will become the inflated view's parent view.

        //add attributes to the seek element
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        rlp.addRule(RelativeLayout.BELOW, R.id.resultLinearLayout);
        rlp.setMargins(0,0,0,0);
        linearLayout.addView(child,rlp);

        seekBar =       findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);
    }

    /**This function get px unit and convert it to DP unit*/
    public int dp2px( int dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics. DENSITY_DEFAULT);
        return (int) px;
    }

    /**This function handle the click on operation buttons*/
    public void operationButtonClicked(View view){
        int btId,  num1, num2;
        float sum = 0;
        String tempString;
        if (!(view instanceof Button)) {
            Log.e("Error:", "view is not an instance of Button");
            return;
        }

        if(checkForEnterNumbers() != true){
            return;
        }

        btId = ((Button)view).getId();
        num1 = Integer.valueOf(edNum1.getText().toString());
        num2 = Integer.valueOf(edNum2.getText().toString());

        switch (btId){
            case R.id.btAdd:
                Log.i("Oparation","Add");
                sum = num1 + num2;
                break;
            case R.id.btSub:
                Log.i("Oparation","Subtraction");
                sum = num1 - num2;
                break;
            case R.id.btMul:
                Log.i("Oparation","Multiplication");
                sum = num1 * num2;
                break;
            case R.id.btDiv:
                Log.i("Oparation","Division");
                if(num2 == 0){
                    Log.e("Error oparation","Attempt divid by zero");
                    showToast(getString(R.string.ERROR_DIVIDE_BY_ZERO));
                    txtResult.setText("");
                    return;
                }
                if(num1 % num2 == 0){
                    sum = num1 / num2;
                }else{
                    sum = num1 / (float) num2;
                }
                break;
        }

        setResultTextView(sum);

    }


    /** This function check if the user enter in both edittext number, if not then show toast message*/
    public boolean checkForEnterNumbers(){
        if(edNum1.getText().length() == 0 || edNum2.getText().length() == 0){
            showToast(getString(R.string.ERROR_OPERANDS_NOT_VALID));
            return false;
        }
        return true;
    }

    /**This function get text and show Toast message*/
    public void showToast(String text){
        Toast toast = Toast.makeText(this,text,Toast.LENGTH_SHORT);
        toast.show();
    }

    private void setAllButtonsEnabled(boolean enabled) {
        btAdd.setEnabled(enabled);
        btSub.setEnabled(enabled);
        btMul.setEnabled(enabled);
        btDiv.setEnabled(enabled);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("res", txtResult.getText().toString());
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        txtResult.setText(savedInstanceState.getString("res"));
    }

    public void clearEditTexts(){
        edNum1.setText("");
        edNum2.setText("");
        showToast(getString(R.string.MSG_CLEAR_ALL_FIELDS));
    }

    public void clearResultField(){
        txtResult.setText("");
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int value, boolean b) {
        resultForamt = String.valueOf(value);
        updateResult();
    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void setResultTextView(float result) {
        lastResult = result;
        updateResult();
    }

    private void updateResult() {
        txtResult.setText(String.format(Locale.getDefault(), "%." + resultForamt + "f", lastResult));
    }

    /*This is Member Class method -> implements TextWatcher*/
    /**This private class implements TextWatcher handle the actions when there is change in the inputs*/
    private class ValidOperandTextWatcher implements TextWatcher
    {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // no action
        }

        /**This function handle the action when the text change in the editText (we do it with the listener)*/
        @Override
        public void afterTextChanged(Editable s) {
            //check if the second editText is zero
            try {
                int operand1 = Integer.parseInt(edNum1.getText().toString());
                int operand2 = Integer.parseInt(edNum2.getText().toString());
                setAllButtonsEnabled(true);

                //int operand2 = Integer.parseInt(s.toString());
                btDiv.setEnabled(operand2 != 0); //check if the second editText is zero
            } catch (NumberFormatException e) {
                setAllButtonsEnabled(false);
            }
        }
    }
}




