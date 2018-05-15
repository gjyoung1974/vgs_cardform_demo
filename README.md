# VGS Android "add new payment card" Demo

This is an **"add payment card form"** for Android

- This demo shows a simple solution to securely add payment cards to an Android application, within your own code.   
- It is very simple to achieve PCI DSS v3.2 compliance with the VGS Secure Proxy and Vault components.   
- Code changes are minimal. Just set VGS as a "proxy" to your mobile backend. Leveraging VGS's Secure Proxy and Vault compomnents will remove the footprint of sensitive data from your application & databases.

A simple method of protecting card data 
- first marshall the payment card object into a JSON a structure : 

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

- Instantiate the Mobile_BackEndClient(URL vgs_proxy_url, String your_mbe_api_endpoint) with the VGS Proxy URL and desired endpoint.    

- Persist the sensitive data in the VGS Vault & get a result at the UI Callback.  


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

- A secure "surrogate" or "token" value is persisted within your mobile backend service.   
- The original Card data is now protected within the VGS Vault for later use. 
- Use the secure "surrogate" or "token" to securely operate on sensistive data without handling it directly.
- Securely authorize payments without directly handling raw/plaintext payment card data in your mobile backend. 
- Safely & securely enrich other business processes, secure data analytics, etc.

Example form:    
![Add Card Dialog](./docs/add_card_form.png)    

Example VGS Dashboard Rules config for this demo:    

In the VGS Dashboard: http://dashboard.verygoodsecurity.com  
 we add rules to intercept and securely transform our data elements    
![VGS Rules Config](./docs/rules.png)   
Select all the fields we post to our mobile backend API & set the secure transform rules

Persist a card results:     
![Persist a card results](./docs/vgs-android.png)    
"Tokenized" data is stored in our Mobile backend and stored as tokens in our local SQLite database       

How to use the stored data?

Later, when we need to securely operate with the card data we can simply perform the reverse operation
Create a new rule to "Reveal" the sensitive data to our upsream processor API endpoint. We simply used the stored tokens as input to the revealing rule. The VGS proxy will securely transform the tokens back to the original value & pass them onto the upstream API.     

Learn more at:
https://www.verygoodsecurity.com    
https://www.verygoodsecurity.com/docs/overview

```
2018
~Gordon
```
