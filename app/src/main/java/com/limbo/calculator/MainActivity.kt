package com.limbo.calculator

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import java.lang.NumberFormatException

// kotlin extension property adds a caching function call behind the hood to get a reference to the view
// subsequent calls to findViewById are cached.. code commented out below shows all boiler plate code that can be removed
// uses R.layout.activity_main from main source set
import kotlinx.android.synthetic.main.activity_main.*

private const val STATE_OPERAND1 = "STATE_OPERAND1"
private const val STATE_PENDING_OPERATION = "PENDING_OPERATION"
private const val STATE_OPERAND1_STORED = "Operand1_Stored"
private const val DEBUG = "Callbacks"

class MainActivity : AppCompatActivity() {
    // tells kotlin we are using a non-nullable value but has deferred initialization
    // will throw an exception if result is referenced in the code when it is not yet initialized
    // used for read/writes
    // private lateinit var result: EditText
    // private lateinit var newNumber: EditText

    // lazy delegation -> defining a function to be called to assign the property
    // function will be called the first time it is accessed
    // all subsequent calls to this variable are cached!
    // by lazy keyword is thread safe by default unless otherwise specified
    // to disable thread safety
    // private val displayOperation by lazy(LazyThreadSafetyMode.NONE) { findViewById<TextView>(R.id.operation) }


    // Variables to hold the operands and type of calculation
    private var operand1: Double? = null
    private var pendingOperation = "="

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(DEBUG, "onCreate called")
//        result = findViewById(R.id.result)
//        newNumber = findViewById(R.id.newNumber)
//
//        // Data input buttons
//        val button0: Button = findViewById(R.id.button0)
//        val button1: Button = findViewById(R.id.button1)
//        val button2: Button = findViewById(R.id.button2)
//        val button3: Button = findViewById(R.id.button3)
//        val button4: Button = findViewById(R.id.button4)
//        val button5: Button = findViewById(R.id.button5)
//        val button6: Button = findViewById(R.id.button6)
//        val button7: Button = findViewById(R.id.button7)
//        val button8: Button = findViewById(R.id.button8)
//        val button9: Button = findViewById(R.id.button9)
//        val buttonDot: Button = findViewById(R.id.buttonDot)
//
//        // Operation buttons
//        val buttonEquals = findViewById<Button>(R.id.buttonEquals)
//        val buttonDivide = findViewById<Button>(R.id.buttonDivide)
//        val buttonMultiply = findViewById<Button>(R.id.buttonMultiply)
//        val buttonMinus = findViewById<Button>(R.id.buttonMinus)
//        val buttonPlus = findViewById<Button>(R.id.buttonPlus)

        // variable listener holds a reference to a View.OnClickListener
        // creates a new instance of View.OnClickListener
        val listener = View.OnClickListener { v ->
            val b = v as Button
            // every time a button is clicked, append the button's text to newNumber which is an editText widget
            newNumber.append(b.text)
        }

        // assign listener reference to each button
        // uses the same listener function reference per button
        button0.setOnClickListener(listener)
        button1.setOnClickListener(listener)
        button2.setOnClickListener(listener)
        button3.setOnClickListener(listener)
        button4.setOnClickListener(listener)
        button5.setOnClickListener(listener)
        button6.setOnClickListener(listener)
        button7.setOnClickListener(listener)
        button8.setOnClickListener(listener)
        button9.setOnClickListener(listener)
        buttonDot.setOnClickListener(listener)

        val opListener = View.OnClickListener { v ->
            val op = (v as Button).text.toString()
            try {
                val value = newNumber.text.toString()
                performOperation(value.toDouble(), op)
            } catch (e: NumberFormatException) {
                newNumber.setText("")
            }
            pendingOperation = op
            operation.text = pendingOperation
        }

        buttonEquals.setOnClickListener(opListener)
        buttonMultiply.setOnClickListener(opListener)
        buttonDivide.setOnClickListener(opListener)
        buttonMinus.setOnClickListener(opListener)
        buttonPlus.setOnClickListener(opListener)
    }

    // onSaveInstanceState with 2 params will only be called if onCreate method also has the same 2 params :( (spent 45+minutes stuck with this)
    // Bundle class represents a hash map (key-value pairs)
    // only called when outState is not null so we can use the following parameter list below
    override fun onSaveInstanceState(outState: Bundle) {
        // 2 edit text widgets are saved for us which is handled by super.onSaveInstanceState
        super.onSaveInstanceState(outState)
        Log.d(DEBUG, "onSaveInstanceState called")
        if(operand1 != null) {
            // !! asserts the object is not null but will throw a null pointer exception if operand1 is null
            outState.putDouble(STATE_OPERAND1, operand1!!)
            outState.putBoolean(STATE_OPERAND1_STORED, true)
        } else if(newNumber.text.isNotEmpty()) {
            outState.putDouble(STATE_OPERAND1, newNumber.text.toString().toDouble())
            outState.putBoolean(STATE_OPERAND1_STORED, true)
        }

        outState.putString(STATE_PENDING_OPERATION, pendingOperation)
    }

    // App destroys activity and recreates it when screen orientation is rotated because screen xml file layouts could be different
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        // 2 edit text widgets are restored for us by super.onRestoreInstanceState
        super.onRestoreInstanceState(savedInstanceState)
        Log.d(DEBUG, "onRestoreInstanceState called")

        if(savedInstanceState.getBoolean(STATE_OPERAND1_STORED, false)) {
            operand1 = savedInstanceState.getDouble(STATE_OPERAND1)
        } else {
            Log.d(DEBUG, "onRestoreInstanceState: operand1 did not have a valid value")
            operand1 = null
        }

        pendingOperation = savedInstanceState.getString(STATE_PENDING_OPERATION, "=")
        operation.text = pendingOperation
    }

    private fun performOperation(value: Double, operation: String) {
        if (operand1 == null) {
            operand1 = value
        } else {
            if (pendingOperation == "=") {
                pendingOperation = operation
            }

            when (pendingOperation) {
                "=" -> operand1 = value
                "/" -> operand1 = if (value == 0.0) {
                    Double.NaN // handle attempt to divide by 0
                } else {
                    operand1!! / value
                }
                "*" -> operand1 = operand1!! * value // gets a null pointer exception if operand1 is null during runtime
                "-" -> operand1 = operand1!! - value
                "+" -> operand1 = operand1!! + value
            }
        }

        result.setText(operand1.toString())
        newNumber.setText("")
    }
}
