# VGS Android "add new payment card" Demo

This is an "Add Payment Card form" for Android

This app demonstrates how simple it is to securely add payment card data to your
app with VeryGoodSecurity


A simple method of protecting card data 
- first marshall the card object as a JSON a strucure: 

```
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
```

2. Instantiate the Mobile_BackEndClient(URL vgs_url, String mbe_endpoint) with the VGS Proxy URL and desired endpoint.    

3. Persist the senstive data as a protected token & get a result at the UI Callback.  


```

            // Mobile Backend Service with VGS Proxy re-write rule: Mobile_BackEndClient(url, mbe_endpoint)
            Mobile_BackEndClient api = new Mobile_BackEndClient(url, mbe_endpoint); //URL reflects VGS proxy
            api.persistSensitive(card.toString(), new MobileBE_UICallback() {
                @Override
                public void onSuccess(String token) {
                    Toast.makeText(mCardForm.getContext(), token, Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onFailure(MobileBE_Error error) {
                    Toast.makeText(mCardForm.getContext(), error.toString(), Toast.LENGTH_SHORT).show();

                }
            });
```

4. the Card data is now protected within the VGS vault for later secured reveal or enrichment operations. 

![Add Card Dialog](./docs/add_card_form.png)

https://www.verygoodsecurity.com/

