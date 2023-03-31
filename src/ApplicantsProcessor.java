/**
 * Pentru acest proiect am folosit doua librarii third party, care se gasesc pe Maven Central si anume:
 *
 * GSON pentru a afisa rezultatul in format JSON
 *      https://mvnrepository.com/artifact/com.google.code.gson/gson
 *
 * Commons-Validator pentru a valida adresele de email
 *      https://mvnrepository.com/artifact/commons-validator/commons-validator
 */

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.apache.commons.validator.routines.EmailValidator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

class Aplicant{
    String name; //atributele clasei
    String email;
    LocalDateTime dateTime;
    double score;

    public Aplicant() { //constructor gol
    }

    public Aplicant(String name, String email, LocalDateTime dateTime, double score) { //constructor cu parametrii
        this.name = name;
        this.email = email;
        this.dateTime = dateTime;
        this.score = score;
    }

    public double getScore() {
        return score;
    }
    public LocalDateTime getDateTime() {
        return dateTime;
    } //getteri pentru scor si data

    @Override
    public String toString() { //metoda toString()
        return "Aplicant{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", dateTime=" + dateTime +
                ", score=" + score +
                '}';
    }
}

public class ApplicantsProcessor {

    //ajustam scorul in functie de data incarcarii
    public static void scoreAdjusments(ArrayList<Aplicant> aplicants){
        sortByDate(aplicants); //sortam dupa data
        Collections.reverse(aplicants); //inversam ordinea

        long firstDay = aplicants.get(0).dateTime.getDayOfMonth(); //stocam prima zi
        long lastDay = aplicants.get(aplicants.size()-1).dateTime.getDayOfMonth(); //stocam ultima zi

        boolean sameDate = false; //o variabila sa ne spuna daca toti au depus in aceeasi zi
        for (int i=1;i<aplicants.size();i++) { //parcurgem lista incepand cu al doilea element
            sameDate = aplicants.get(i).dateTime.getDayOfMonth() == firstDay; //comparam lista cu prima zi, iar daca toti au depus in aceeasi zi devine true
        }

        if(!sameDate){ //daca nu au depus toti in aceaaasi zi
            for(Aplicant i : aplicants) //parcurgem toata lista
                if(i.dateTime.getDayOfMonth() == firstDay) { //daca a aplicat in prima zi
                    i.score++; //crestem scorul cu 1 punct
                    if(i.score > 10) //daca scorul este peste 10
                        i.score = 10; //il lasam 10
                }else if(i.dateTime.getDayOfMonth() == lastDay) { //daca a aplicat in ultima zi
                    if(i.dateTime.getHour() > 12) //daca ora aplicarii este dupa mijlocul zilei
                        i.score --; //scadem un punct
                }
        }
    }

    //average score pentru prima jumatate
    public static double findAverageScore(ArrayList<Aplicant> aplicants){
        aplicants.sort(Comparator.comparing(Aplicant::getScore)); //sortam lista dupa scor
        Collections.reverse(aplicants); //inversam ordinea

        //in caz ca avem numar impar de aplicanti, o sa facem impartirea si rotunjim in sus
        double half = (int) Math.ceil((double)aplicants.size() / 2); //numarul de aplicanti in prima jumatate
        double sum = 0; //suma scorurilor

        for(int i=0;i<half;i++) //parcurgem lista pana la prima jumatate
            sum += aplicants.get(i).score; //insumam scorurile
        return sum/half; //returna media
    }

    //aplicantii unici
    public static int findUniqueAplicants(ArrayList<Aplicant> aplicants){

        int uniqueApplicants = 0; //numarul de aplicanti unici
        for(Aplicant i : aplicants) { //parcurgem lista
            if(Collections.frequency(aplicants, i) < 2) //daca avem doar o singura aparitie
                uniqueApplicants++; //incrementam numarul de aplicanti unici
        }
        return uniqueApplicants;
    }

    //top 3 aplicanti
    public static String[] findTopApplicants(ArrayList<Aplicant> aplicants){
        //in caz ca nu avem cel putin 3 aplicanti trebuie sa luam toata dimensiunea listei
        int lenght = Math.min(aplicants.size(), 3); //minimul dintre cele doua

        String[] topAplicants = new String[lenght]; //creeam un array string pentru a stoca aplicantii

        aplicants.sort(Comparator.comparing(Aplicant::getScore)); //sortam lista dupa scor
        Collections.reverse(aplicants); //o pune in ordine inversa

        for(int i=0;i<lenght;i++) { //parcurgem lista
            String[] str = aplicants.get(i).name.split(" "); //despartim numele in cuvinte
            topAplicants[i] = str[str.length-1]; //punem in noul array doar numele de familie
        }
        return topAplicants; //returnam array-ul
    }

    //sortam dupa data
    public static void sortByDate(ArrayList<Aplicant> aplicants){
        aplicants.sort(Comparator.comparing(Aplicant::getDateTime)); //sortam lista dupa scor
        Collections.reverse(aplicants); //o pune in ordine inversa
    }

    //eliminam persoanele care au aplicat de mai multe ori cu acelasi email si luam in considerare doar ultima aplicare
    public static void emailDuplicates(ArrayList<Aplicant> aplicant){

        Collections.reverse(aplicant); //inversam ordinea
        //sortByDate(aplicant); //daca se elimina toate inafara de ultima aplicare dupa data, le sortam dupa data inainte

        for(int i=0; i<aplicant.size();i++) { //parcurgem lista
            for(int j=i+1;j<aplicant.size();j++) { //cautam alte aparitii inafara de ultima
                if(aplicant.get(i).email.equals(aplicant.get(j).email)) { //daca gasim
                    aplicant.remove(j); //eliminam din lista
                }
            }
        }
    }

    //verificam ca numele sa fie conform cerintelor
    public static boolean nameProcessor(String name){
        return name.split(" ").length > 1; //daca are mai mult de doua nume returneaza true
    }

    //verificam ca email-ul sa fie conform cerintelor
    public static boolean emailProcessor(String email){
        return EmailValidator.getInstance().isValid(email); //returneaza true daca email-ul este valid
    }

    //verificam daca data este conform cerintelor
    public static boolean dateProcessor(String time){
        if(time.indexOf('T') == -1)
            return false;
        try { //incercam sa aplicam parse()
            LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        } catch (Exception e) { //daca nu se poate realiza si avem exceptie
            return false; //returnam false
        }
        return true; //in caz contrar returnam true
    }

    //verificam daca scorul este conform cerintelor
    public static boolean scoreProcessor(String score){
        try { //incercam sa aplicam parseDouble()
            Double.parseDouble(score);
        }catch (Exception e){ //in caz ca avem exceptie
            return false; //returnam false
        }
        return true; //in caz contrar returnam true
    }

    /**
     *
     * @param csvStream input stream allowing to read the CSV input file
     * @return the processing output, in JSON format
     */
    public static String  processApplicants(InputStream csvStream) throws IOException {

        ArrayList<Aplicant> aplicant = new ArrayList<>(); //creeam o noua lista pentru a putea adauga fiecare aplicant
        BufferedReader reader = new BufferedReader(new InputStreamReader(csvStream)); //obiect pentru a putea citi din fisier

        while(reader.ready()) { //cat timp putem citi
            String line = reader.readLine(); //citim linia in variabila line

            if(!line.matches("^[A-Z].*$")) { //daca linia nu incepe cu o litera de la A la Z
                line = reader.readLine(); //trecem la linia urmatoare
            }

            String[] str = line.split(","); //intr-un string array punem linia pe care o despartim la virgula

            if (nameProcessor(str[0]) && emailProcessor(str[1]) && dateProcessor(str[2]) && scoreProcessor(str[3])){
                //daca toate criteriile sunt indeplinite
                String name = str[0]; //punem fiecare valoare intr-o variabila
                String email = str[1];
                LocalDateTime dateTime = LocalDateTime.parse(str[2]); //convertim din string in LocalDateTime
                double score = Double.parseDouble(str[3]); //convertim din string in double

                aplicant.add(new Aplicant(name,email,dateTime,score)); //in lista adaugam un nou obiect de tip Aplicant cu parametrii de mai sus
            }
        }

        emailDuplicates(aplicant); //eliminam toate email-urile care apar de mai multe ori

        int uniqueApplicants = findUniqueAplicants(aplicant); //luam numarul de aplicanti unici
        String[] topApplicants = findTopApplicants(aplicant); //luam numele celor 3 aplicanti din top
        double averageScore = findAverageScore(aplicant); //calculam scorul mediu

        scoreAdjusments(aplicant); //facem ajustaje la scor

        //returnam un string in format JSON
        return "{\"uniqueApplicants\" :\"" + uniqueApplicants + "\" , " + "\"topApplicants\" :\"" + Arrays.stream(topApplicants).toList() + "\" , " + "\"averageScore\" :\"" + averageScore +"\" }";
    }

    public static void main(String[] args) throws IOException {

        InputStream csvStream = new FileInputStream("src/date.csv"); //creeam un obiect de tip InputStream pentru a citi din fisier
        String jsonString = processApplicants(csvStream); //punem rezultatul in string

        Gson gson = new GsonBuilder().setPrettyPrinting().create(); //creeam un obiect gson pentru a printa in format JSON
        JsonElement je = JsonParser.parseString(jsonString);
        String prettyJsonString = gson.toJson(je);
        System.out.println(prettyJsonString); //afisam reuzltatul

        // System.out.println(dateProcessor("2023-01-24 16:32:19"));
    }
}

