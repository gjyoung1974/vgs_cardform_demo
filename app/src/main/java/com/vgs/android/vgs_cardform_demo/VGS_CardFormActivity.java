package com.vgs.android.vgs_cardform_demo;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.braintreepayments.cardform.OnCardFormSubmitListener;
import com.braintreepayments.cardform.utils.CardType;
import com.braintreepayments.cardform.view.CardEditText;
import com.braintreepayments.cardform.view.CardForm;
import com.braintreepayments.cardform.view.SupportedCardTypesView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import static com.vgs.android.vgs_cardform_demo.CardStorageContract.CardEntry.COLUMN_NAME_CARDID;
import static com.vgs.android.vgs_cardform_demo.CardStorageContract.CardEntry.COLUMN_NAME_CARDTYPE;
import static com.vgs.android.vgs_cardform_demo.CardStorageContract.CardEntry.COLUMN_NAME_CARD_CCN;
import static com.vgs.android.vgs_cardform_demo.CardStorageContract.CardEntry.COLUMN_NAME_CARD_CVV;

public class VGS_CardFormActivity extends AppCompatActivity implements OnCardFormSubmitListener,
        CardEditText.OnCardTypeChangedListener {

    private static final CardType[] SUPPORTED_CARD_TYPES = {CardType.VISA, CardType.MASTERCARD, CardType.DISCOVER,
            CardType.AMEX, CardType.DINERS_CLUB, CardType.JCB, CardType.MAESTRO, CardType.UNIONPAY};

    private SupportedCardTypesView mSupportedCardTypesView;

    protected CardForm mCardForm;

    //open a SQLite DB for local storage:
    CardStorageDBHelper mDbHelper = new CardStorageDBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_form);

        mSupportedCardTypesView = findViewById(R.id.supported_card_types);
        mSupportedCardTypesView.setSupportedCardTypes(SUPPORTED_CARD_TYPES);

        mCardForm = findViewById(R.id.card_form);
        mCardForm.cardRequired(true)
                .maskCardNumber(true)
                .maskCvv(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .postalCodeRequired(true)
                .mobileNumberRequired(true)
                .mobileNumberExplanation("Make sure SMS is enabled for this mobile number")
                .actionLabel(getString(R.string.purchase))
                .setup(this);

        mCardForm.setOnCardFormSubmitListener(this);
        mCardForm.setOnCardTypeChangedListener(this);

        // Warning: this is for development purposes only and should never be done outside of this example app.
        // Failure to set FLAG_SECURE exposes your app to screenshots allowing other apps to steal card information.
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
    }

    @Override
    public void onCardTypeChanged(CardType cardType) {
        if (cardType == CardType.EMPTY) {
            mSupportedCardTypesView.setSupportedCardTypes(SUPPORTED_CARD_TYPES);
        } else {
            mSupportedCardTypesView.setSelected(cardType);
        }
    }

    @Override
    public void onCardFormSubmit() {


        URL url = null;
        String mbe_endpoint = this.getString(R.string.mbe_endpoint);
        try {
            //todo: get this property from another means
            url = new URL(this.getString(R.string.proxy_url));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        if (mCardForm.isValid()) {

            JSONObject card = new JSONObject();
            try {
                card.put("CCN", mCardForm.getCardNumber());
                card.put("CVV", mCardForm.getCvv());
                card.put("MONTH", mCardForm.getExpirationMonth());
                card.put("YEAR", mCardForm.getExpirationYear());
                card.put("POST_CODE", mCardForm.getPostalCode());
                card.put("COUNTRYCODE", mCardForm.getCountryCode());
                card.put("MOBILE", mCardForm.getMobileNumber());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Mobile Backend Service with VGS Proxy re-write rule: Mobile_BackEndClient(url)
            Mobile_BackEndClient api = new Mobile_BackEndClient(url, mbe_endpoint); //URL reflects VGS proxy
            api.persistSensitive(card.toString(), new MobileBE_UICallback() {
                @Override
                public void onSuccess(String token) {

                    // Parse the result from VGS proxy
                    JSONObject jObject = new JSONObject();
                    try {
                        jObject = new JSONObject(token);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // Gets the data repository in write mode
                    SQLiteDatabase db = mDbHelper.getWritableDatabase();

                    // Create a new map of values, where column names are the keys
                    ContentValues values = new ContentValues();
                    values.put(COLUMN_NAME_CARDID, 1);
                    values.put(COLUMN_NAME_CARDTYPE, 1); //fix this get from form validation
                    try {
                        values.put(COLUMN_NAME_CARD_CCN, jObject.getString("CCN"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        values.put(COLUMN_NAME_CARD_CVV, jObject.getString("CVV"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // Insert the new row, returning the primary key value of the new row
                    long newRowId = db.insert("card", null, values);

                    // Print the result locally
                    Toast.makeText(mCardForm.getContext(), token, Toast.LENGTH_LONG).show();


                }

                @Override
                public void onFailure(MobileBE_Error error) {
                    Toast.makeText(mCardForm.getContext(), error.toString(), Toast.LENGTH_SHORT).show();

                }
            });

        } else {
            // mCardForm.validate(); do someting as a fallback?
            Toast.makeText(this, R.string.invalid, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.card_io_item) {
            mCardForm.scanCard(this);
            return true;
        }

        return false;
    }
}
