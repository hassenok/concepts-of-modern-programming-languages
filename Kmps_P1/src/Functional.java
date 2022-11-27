import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Functional {



    public static ArrayList<Album> parseFile (ArrayList<String> token_list, ArrayList<Album> albums, Boolean is_in_album, Boolean is_in_track, int current_album, int current_track, int counter)  {

        if(counter < token_list.size()) {

            if(token_list.get(counter).equals("album")){
                albums.add(new Album());
                return parseFile (token_list, albums, true, is_in_track, albums.size()-1, current_track,  counter+1);
            }
            else if(token_list.get(counter).equals("/album")){
                return parseFile (token_list, albums, false, is_in_track, current_album, current_track,  counter+1);
            }
            else if(token_list.get(counter).equals("title")){
                if(is_in_track) {
                    albums.get(current_album).tracks.get(current_track).title = token_list.get(counter+1);}
                else if(is_in_album) {
                    albums.get(current_album).title = token_list.get(counter+1);}
                return parseFile (token_list, albums, is_in_album, is_in_track, current_album, current_track,  counter+1);
            }
            else if(token_list.get(counter).equals("artist")){
                albums.get(current_album).artist = token_list.get(counter+1);
                return parseFile (token_list, albums, is_in_album, is_in_track, current_album, current_track,  counter+1);
            }
            else if(token_list.get(counter).equals("rating")){
                albums.get(current_album).tracks.get(current_track).rating = Integer.parseInt(token_list.get(counter+1));
                return parseFile (token_list, albums, is_in_album, is_in_track, current_album, current_track,  counter+1);
            }
            else if(token_list.get(counter).equals("track")){
                albums.get(current_album).tracks.add(new Track());

                return parseFile (token_list, albums, is_in_album, true, current_album, albums.get(current_album).tracks.size()-1,  counter+1);
            }
            else if(token_list.get(counter).equals("/track")){
                return parseFile (token_list, albums, is_in_album, false, current_album, current_track,  counter+1);
            }
            else if(token_list.get(counter).equals("feature")){
                albums.get(current_album).tracks.get(current_track).features.add(token_list.get(counter+1));
                return parseFile (token_list, albums, is_in_album, is_in_track, current_album, current_track,  counter+1);
            }
            else if(token_list.get(counter).equals("length")){
                albums.get(current_album).tracks.get(current_track).length = token_list.get(counter+1);
                return parseFile (token_list, albums, is_in_album, is_in_track, current_album, current_track,  counter+1);
            }
            else if(token_list.get(counter).equals("writing")){
                albums.get(current_album).tracks.get(current_track).writers.add(token_list.get(counter+1));
                return parseFile (token_list, albums, is_in_album, is_in_track, current_album, current_track,  counter+1);
            }
            else if(token_list.get(counter).equals("date")){
                albums.get(current_album).date = token_list.get(counter+1);
                return parseFile (token_list, albums, is_in_album, is_in_track, current_album, current_track,  counter+1);
            }
            else {
                return parseFile (token_list, albums, is_in_album, is_in_track, current_album, current_track, counter+1);
            }

        }

        return albums;

    }




public static ArrayList<String> createTokenList(byte[] file_contents,
                           ArrayList<String> token_list, int current_character,String erststring){
        if(current_character<file_contents.length){
    if (file_contents[current_character] ==9 ||file_contents[current_character] ==10||file_contents[current_character] ==13) {
       return  createTokenList(file_contents,token_list,current_character+1,erststring);
    }else if (file_contents[current_character] ==60||file_contents[current_character] ==62){
        if(erststring!="")
        token_list.add(erststring);
        //open_close= (!open_close);
        erststring="";
        return createTokenList(file_contents,token_list,current_character+1,erststring);
    }else{
        erststring=erststring+(char)file_contents[current_character];
        return createTokenList(file_contents,token_list,current_character+1,erststring);
    }


}
    return token_list;
    }



    public static void main(String[] args) throws IOException {

         ArrayList<String> token_list = new ArrayList<String>();
         ArrayList<Album> albums = new ArrayList<Album>();
         final Boolean is_in_album = false;
         final Boolean is_in_track = false;
         final  int current_album = 0;
         final int current_track = 0;
         final int current_character = 0;
        //static String home = System.getProperty("user.home");
         String file_path = ("alben.xml");
         //boolean open_close=false;
         //String erststring="";

        byte[] file_contents = Files.readAllBytes(Paths.get(file_path));

        //token_list = createTokenList(file_contents, token_list);
        token_list = createTokenList(file_contents, token_list,0,"");
        albums =  parseFile(token_list, albums, is_in_album, is_in_track, current_album, current_track, current_character);



        for(int i = 0; i < albums.size(); i++){
            System.out.println(albums.get(i));
        }

        /*for(int i = 0; i < token_list.size(); i++){
            System.out.println(token_list.get(i));
        }*/

    }
}