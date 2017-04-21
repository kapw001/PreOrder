package com.zaafoo.preorder.activities;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.zaafoo.preorder.R;
import com.zaafoo.preorder.models.Contact;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.paperdb.Paper;


public class ShareUsActivity extends AppCompatActivity {

    Button shareUS;
    StringBuilder sb;
    JSONObject data;
    ArrayList<Contact> contacts;
    TextView tv;
    String offer="invalid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_us);
        Paper.init(this);
        shareUS = (Button) findViewById(R.id.button7);
        tv=(TextView)findViewById(R.id.textView20);
        checkIfPromoIsAvailable();

    }

    private void checkIfPromoIsAvailable() {
        final ProgressDialog pd=new ProgressDialog(this);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();
        AndroidNetworking.get("http://zaafoo.com/promo/")
                .addPathParameter("pageNumber", "0")
                .addQueryParameter("limit", "3")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String promo=response.getString("value");
                            if(promo.equalsIgnoreCase("true")) {
                                offer="true";
                            }
                            else
                                offer="false";
                            loadActivity();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        pd.dismiss();
                    }

                    @Override
                    public void onError(ANError anError) {
                    pd.dismiss();
                    }
                });
    }

    private JSONObject constructJSONData(ArrayList<Contact> conEmails, ArrayList<Contact> conNumbers) {
        JSONObject contactData = new JSONObject();
        JSONArray emailArray = new JSONArray();
        JSONArray phoneArray = new JSONArray();
        JSONObject phoneObject;
        JSONObject emailObject;

        for (Contact x : conEmails) {
            emailObject = new JSONObject();
            try {
                emailObject.put("name", x.getName());
                emailObject.put("email", x.getEmail());
                emailArray.put(emailObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        for(Contact y :conNumbers){
            phoneObject = new JSONObject();
            try {
                phoneObject.put("name", y.getName());
                phoneObject.put("phno", y.getNumber());
                phoneArray.put(phoneObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            contactData.put("emails", emailArray);
            contactData.put("phnos", phoneArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return contactData;
    }

    private void sendContactDataToZaafoo(JSONObject data) {
        String token = Paper.book().read("token");
        AndroidNetworking.post("http://zaafoo.com/storeemailphno/")
                .addJSONObjectBody(data)
                .addHeaders("Authorization", "Token "+token)// posting json
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ShareUsActivity.this, "Successfully Shared", Toast.LENGTH_LONG).show();
                            }
                        });
                        Paper.book().write("share","true");


                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }

    private void createThankYouDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Thank You");
        builder.setMessage("You Will Get 10% Off On Your Next Booking..");
        builder.setCancelable(false);
        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog=builder.create();
        dialog.show();

    }

    public ArrayList<Contact> getAllEmails() {

        ArrayList<Contact> contacts = new ArrayList<>();
        Contact c;
        ContentResolver cr = getBaseContext().getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String contactId = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name=cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                Cursor emails = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null);
                while(emails.moveToNext()){
                    c=new Contact();
                    String emailAddress = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    if(emailAddress.length()>20)
                        continue;
                    c.setName(name);
                    c.setEmail(emailAddress);
                    contacts.add(c);
                }
                emails.close();
            }
            cur.close();
        }
        return contacts;
    }


    public void loadActivity() {
        // Check For Permission
        String[] perms = {"android.permission.READ_CONTACTS"};
        int permission = ActivityCompat.checkSelfPermission(this, "android.permission.READ_CONTACTS");
        if ((permission == PackageManager.PERMISSION_GRANTED)) {

            shareUS.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(offer.equalsIgnoreCase("invalid"))
                        Toast.makeText(ShareUsActivity.this,"Check Your Internet Connection..",Toast.LENGTH_SHORT).show();
                    else if (offer.equalsIgnoreCase("false"))
                        Toast.makeText(ShareUsActivity.this,"No Offers Available Now..",Toast.LENGTH_SHORT).show();
                    else if(offer.equalsIgnoreCase("true")) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                ArrayList<Contact> conMails = getAllEmails();
                                ArrayList<Contact> conNumbers=getAllNumbers();
                                data = constructJSONData(conMails,conNumbers);
                                sendContactDataToZaafoo(data);
                            }
                        }).start();
                        createThankYouDialog();

                    }
                }
            });
        }
        else
            ActivityCompat.requestPermissions(this, perms, 200);


    }

    private ArrayList<Contact> getAllNumbers() {
        contacts=new ArrayList<>();
        Contact c;
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        while (phones.moveToNext())
        {
            String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            c=new Contact();
            c.setName(name);
            c.setNumber(phoneNumber);
            contacts.add(c);
        }
        phones.close();
        return contacts;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){

            case 200:
                boolean contactsAccepted = grantResults[0]==PackageManager.PERMISSION_GRANTED;
                loadActivity();
                break;

        }
    }
}
