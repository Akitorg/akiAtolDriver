# akiAtolDriver

Adapter and some visual part for using atol kkm library.

Supports both 9 and 10 versions.

UPDATE 9 version is no longer supported.

## Getting Started

### Advantage

#### Print KKM Activity to show user


<img src="https://mobilecash.akitorg.ru/images/screen_1.png" alt="drawing" width="250"/>


#### Business logic released, such as:

1. Slice by vat system
2. Discount math
3. Accuracy math
4. KKM emulation
5. Income, outcome, correction, Z/X-Reports
6. Service to check count of un send to OFD docs

    //Проверка неотправленных в ОФД заказов
    
    startService(new Intent(MainActivity.this, OFDService.class));
    registerReceiver(ofdUnsendReceiver, new IntentFilter("ofdUnsend"));
    
#### Huge settings amount


<img src="https://mobilecash.akitorg.ru/images/screen_2.png" alt="drawing" width="250"/>

#### Theme settings:

    PrintChequeActivity.setDarkTheme();
    PrintChequeActivity.setLightTheme();
    

<img src="https://mobilecash.akitorg.ru/images/screen_3.png" alt="drawing" width="250"/> <img src="https://mobilecash.akitorg.ru/images/screen_4.png" alt="drawing" width="250"/>
    

### Installing

#### Using the Gradle

Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
	
#### Step 2. Add the dependency

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
    
#### Step 2. Add the dependency

	<dependency>
        <groupId>com.github.Akitorg</groupId>
        <artifactId>akiAtolDriver</artifactId>
        <version>-SNAPSHOT</version>
    </dependency>
    
## IMPORTANT

Because this repo is private you need also do this steps:

#### Step 1. Authorize JitPack and get your personal access token:
    
    jp_s8m7m2t3q9jqdnft6c4jv9hg2u
    
#### Step 2. Add the token to $HOME/.gradle/gradle.properties
    								
    								
     authToken=jp_s8m7m2t3q9jqdnft6c4jv9hg2u
     
    							
  Then use authToken as the username in your build.gradle:
    
    								
    								
     allprojects {
        repositories {
            ...
            maven {
                url "https://jitpack.io"
                credentials { username authToken }
            }
        }
     }
    		
    
### Using


#### Step 1. To show user settings screen (fragment)



	getFragmentManager().beginTransaction().replace(R.id.content_frame, new SettingsKKMFragment())
	.addToBackStack("SettingsKKM").commit();


    
#### Step 2. To print cheque start PrintChequeActivity with extras printType and printObject



    PrintType pType = PrintType.ORDER_CASH;
    PrintObjects.Order order = new PrintObjects.Order(sale_extid, goods, docSum,
                    full_summ, ChequeType.FULL_PAY, mail, clientName, clientInn, needCopy);

    Intent intent = new Intent(getContext(), PrintChequeActivity.class);
    
    intent.putExtra("printType", pType);
    intent.putExtra("printObject", order);

    getActivity().startActivityForResult(intent, PRINT_RESPONSE_CODE);


    
To make for example return of order simple change PrintType


        
    PrintType pType = PrintType.RETORDER_CASH;


    
#### Step 3. After Activity done it will give the result



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

                        // HERE YOU CAN SAVE DOCS OR DO WHAT EVER YOU WANT
                        DBRequests.saveDoc(this, printType, printObject, chequeNumber);
                    }

                }  
                
                break;
            }  
        }        
    }


      
#### Step 4. To make income/outcome or report you can call KKM_Info fragment



    Fragment frag = new KKM_Fragment();
    
    FragmentManager fm = getSupportFragmentManager();
    fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    fm.beginTransaction().replace(R.id.content_frame, frag).commit();


    
Anyway if you want to do it bu yourself for some reason

    Intent intent = new Intent(getContext(), PrintChequeActivity.class);
    
    intent.putExtra("printType", PrintType.ZREP);           
    intent.putExtra("printObject", new PrintObjects.ZRep()); // Close session is called in this example

    getActivity().startActivityForResult(intent, PRINT_RESPONSE_CODE);    

PrintObjects.Order constructor in this example get this params

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

Here you can see the values of [a PrintType](https://github.com/Akitorg/akiAtolDriver/blob/master/app/src/main/java/com/ex/akiatol/print/PrintType.java) and [a PrintObjects](https://github.com/Akitorg/akiAtolDriver/blob/master/app/src/main/java/com/ex/akiatol/print/PrintObjects.java)

