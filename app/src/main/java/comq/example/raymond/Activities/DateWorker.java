package comq.example.raymond.Activities;

public class DateWorker {

    public static String date = "";
    public static String processDate(int day, int month, int year){
        if (month == 1){
            date = "January" + " " + day + ", " + year;
        }else if (month == 2){
            date = "February" + " " + day + ", " + year;
        }
        else if (month == 3){
            date = "March" + " " + day + ", " + year;
        }else if (month == 4){
            date = "April" + " " + day + ", " + year;
        }else if (month == 5){
            String date = "May" + " " + day + ", " + year;
        }else if (month == 6){
            date = "June" + " " + day + ", " + year;
        }else if (month == 7){
            date = "July" + " " + day + ", " + year;
        }else if (month == 8){
            date = "August" + " " + day + ", " + year;
        }else if (month == 9){
            date = "September" + " " + day + ", " + year;
        }else if (month == 10){
            date = "October" + " " + day + ", " + year;
        }else if(month == 11){
            date = "November" + " " + day + ", " + year;
        }else if(month == 12){
            date = "December" + " " + day + ", " + year;
        }
        return date;
    }
}
