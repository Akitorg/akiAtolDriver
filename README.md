# akiAtolDriver

Adapter and some visual part for using atol kkm library

## Getting Started

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

Step 1. First of all we need to create Fragment that extends <b>PrintChequeFragment</b> to save part realisation

	public class PrintFragment extends PrintChequeFragment {
    
        @Override
        public boolean onPrintDone() {
            return false;
        }
    
        @Override
        public void sendDebugLogs() {
    
        }
    
    }
    
 Step 2. Set constants for correct lib work.  
