public static void guessMyBirthday(){
Scanner sc = new Scanner(System.in);
int min = 0;
int max = 13;
int guess;
String prompt;// creating variables 
boolean correcto = false;
int month=0;
int day;
int year=0;
while(correcto == false){ // while boolean is false it will guess until it is true using binary search
guess = (min+max)/2; // average value 
System.out.print("Is your birthday in " + monthToString(guess) + "? Y/n ");
prompt = sc.nextLine();
if(prompt.equals("Y")){
month = guess;
correcto = true;
} else if(prompt.equals("n")) {
System.out.print("Is your birthday after " + monthToString(guess) + "? Y/n ");
prompt = sc.nextLine();
if(prompt.equals("Y")){
min = guess;
} else if(prompt.equals("n")) {
max = guess;
} else {
System.out.println("Invalid input");
}
} else {
System.out.println("Invalid input");
}
}
// check leap year 
if(month==2){
System.out.print("What year were you born? ");
year = sc.nextInt();
} else {
year = 0;
}

// reset the variable values for days 
correcto=false;
min = 0;
max = numDaysInMonth(month,year);
// while boolean is false 
while(correcto == false){
guess = (min+max)/2;
System.out.print("Is your birthday on " + monthToString(month) + " " + guess + "? Y/n ");
prompt = sc.nextLine();
if(prompt.equals("Y")){
month = guess;
correcto = true;
} else if(prompt.equals("n")) {
System.out.print("Is your birthday after " + monthToString(month) + " " + guess +  "? Y/n ");
prompt = sc.nextLine();
if(prompt.equals("Y")){
min = guess;
} else if(prompt.equals("n")) {
max = guess;
} else {
System.out.println("Invalid input");
}
} else {
System.out.println("Invalid input");
}
}

}
