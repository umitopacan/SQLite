package com.mis49m.sqlite;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

    DatabaseHandler db;
    TextView tvCount;
    EditText etID, etName, etPhone;
    Button btnAdd, btnUpdate, btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // create databasehandler
        db=new DatabaseHandler(getApplicationContext());

        //-- read ui references
        tvCount = (TextView) findViewById(R.id.tv_count);
        etID = (EditText) findViewById(R.id.txt_id);
        etName = (EditText) findViewById(R.id.txt_name);
        etPhone = (EditText) findViewById(R.id.txt_phone);
        etPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        btnAdd = (Button) findViewById(R.id.btn_add);
        btnUpdate = (Button) findViewById(R.id.btn_update);
        btnDelete = (Button) findViewById(R.id.btn_delete);

        showContactCounts();
        makeUpdateForm(false);
    }

    public void delete(View view){
        String id = etID.getText().toString();

        Contact c =new Contact(Integer.valueOf(id), "", "");
        int r = db.deleteContact(c);

        if(r>0){
            showMessage("Contact is deleted!");
            showContactCounts();
            clearValues();
        }else{
            showMessage("Error!");
        }
    }

    public void update(View view){
        String id = etID.getText().toString();
        String name = etName.getText().toString();
        String phone = etPhone.getText().toString();

        Contact c =new Contact(Integer.valueOf(id), name, phone);
        int r = db.updateContact(c);

        if(r>0){
            showMessage("Contact is updated!");
            clearValues();
        }else{
            showMessage("Error!");
        }
    }

    public void getContact(View view){
        String id = etID.getText().toString();

        Contact c = db.getContact(Integer.valueOf(id));
        etName.setText(c.getName());
        etPhone.setText(c.getPhoneNumber());

        makeUpdateForm(true);
    }

    public void add(View view){
        String name = etName.getText().toString();
        String phone = etPhone.getText().toString();

        Contact c =new Contact(0, name, phone);
        long r=db.addContact(c);

        if(r>0){
            showMessage("Contact is inserted!");
            showContactCounts();
            clearValues();
        }else{
            showMessage("Error!");
        }

    }

    private void showContactCounts(){
        tvCount.setText("Contacts count : " + db.getContactsCount());
    }

    private void clearValues(){
        etID.setText("");
        etName.setText("");
        etPhone.setText("");

        makeUpdateForm(false);
    }

    private void makeUpdateForm(boolean isUpdate){
        btnAdd.setEnabled(!isUpdate);
        btnUpdate.setEnabled(isUpdate);
        btnDelete.setEnabled(isUpdate);
    }

    private void showMessage(String value){
        Toast.makeText(getApplicationContext(), value, Toast.LENGTH_SHORT).show();
    }
}
