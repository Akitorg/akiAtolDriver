# akiAtolDriver

Adapter and some visual part for using atol kkm library

## Getting Started

### Advantage

#### Print KKM Activity to show user

    ![alt text](https://mobilecash.akitorg.ru/images/screen_1.png)

#### Business logic released, such as:

    Sno slice
    
    Discount math
    
    Accuracy math
    
#### Huge settings amount

    ![alt text](https://mobilecash.akitorg.ru/images/screen_2.png)

#### Theme settings:

    PrintChequeActivity.setDarkTheme();
    PrintChequeActivity.setLightTheme();
    
    ![alt text](https://mobilecash.akitorg.ru/images/screen_3.png)
    ![alt text](https://mobilecash.akitorg.ru/images/screen_4.png)
    

### Installing

#### Using the Gradle

Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
	
Step 2. Add the dependency

	dependencies {
	        implementation 'com.github.Akitorg:akiAtolDriver:-SNAPSHOT'
	}

#### Using the Maven

	<repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>
    
Step 2. Add the dependency

	<dependency>
        <groupId>com.github.Akitorg</groupId>
        <artifactId>akiAtolDriver</artifactId>
        <version>-SNAPSHOT</version>
    </dependency>
    
### Using

Step 1. To show user settings screen (fragment)

	getFragmentManager().beginTransaction().replace(R.id.content_frame, new SettingsKKMFragment())
	.addToBackStack("SettingsKKM").commit();
    
Step 2. To print cheque start PrintChequeActivity with extras printType and printObject

    PrintType pType = PrintType.ORDER_CASH;
    PrintObjects.Order order = new PrintObjects.Order(sale_extid, goods, docSum,
                    full_summ, ChequeType.FULL_PAY, mail, clientName, clientInn, needCopy);

    Intent intent = new Intent(getContext(), PrintChequeActivity.class);
    
    intent.putExtra("printType", pType);
    intent.putExtra("printObject", order);

    getActivity().startActivityForResult(intent, PRINT_RESPONSE_CODE);
    
To make for example return of order simple change PrintType
        
    PrintType pType = PrintType.RETORDER_CASH;
    
Step 3. After Activity done it will give the result

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){
            case IntentIntegrator.PRINT_RESPONSE_CODE:
            
                if (data != null && data.getExtras() != null) {
                
                    PrintType printType =
                            (PrintType) data.getExtras().getSerializable("printType");

                    HashMap<Integer, PrintObjects> printObjectsSNO =
                            (HashMap<Integer, PrintObjects>) data.getExtras().getSerializable("printObject");

                    long chequeNumber = data.getExtras().getLong("cheque_number");

                    for (HashMap.Entry<Integer, PrintObjects> entry: printObjectsSNO.entrySet()) {

                        // HERE YOU CAN SAVE DOCS OR DO WHAT EVE YOU WANT
                        DBRequests.saveDoc(this, printType, printObject, chequeNumber);
                    }

                }  
                
                break;
            }  
        }        
    }
    
Step 4. To make income/outcome or report you can call KKM_Info fragment

    Fragment frag = new KKM_Fragment();
    
    FragmentManager fm = getSupportFragmentManager();
    fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    fm.beginTransaction().replace(R.id.content_frame, frag).commit();
    
Anyway if you want to do it bu yourself for some reason

    Intent intent = new Intent(getContext(), PrintChequeActivity.class);
    
    intent.putExtra("printType", PrintType.ZREP);                           //Close session is called in this example
    intent.putExtra("printObject", new PrintObjects.ZRep());

    getActivity().startActivityForResult(intent, PRINT_RESPONSE_CODE);    

PrintObjects.Order constructor in this example get this params

    new PrintObjects.Order(
    sale_extid  - uuid of order
    goods       - array of object OrderGood
    docSum      - sum of doc
    get_sum     - sum that client payed (may be less than docSum)
    type        - chequeType
    mail        - email/phone
    clientName  - name of client
    clientInn   - name of client
    needCopy    - print copy of cheque
    
PrintObjects.OrderGood constructor in this example get this params

    extid     - uuid of good 
    name      - good name
    price     - good price (double)
    count     - good count (double) 
    discount  - position discount                          
    unitname  - good unit name 
    vat       - vat count
    vat_sum   - vat sum   
    dsum      - position sum
    sno       - duty type
    type      - good type
    isImport  - is good import? 
    country   - country code (for import) 
    decNumber - declaration number (for import) 
