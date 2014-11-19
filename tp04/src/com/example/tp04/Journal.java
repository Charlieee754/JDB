package com.example.tp04;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.example.tp04.evenementJournal;

import android.content.Context;
import android.text.format.DateFormat;


public class Journal {
	public String Name;
	public File Path;
	public List<evenementJournal> JournaldeBord;
	
	//Constructeur
	public Journal(String name){
		Name = name; // on initialise le tout comme � l'habitude
		JournaldeBord = new ArrayList<evenementJournal>(); // "
	}
	
	/**
	*
	* @author Charles Perreault
	*
	* Cr�e un nouvel �v�nement
	*/
	public void newEvent(int EventType, String Data){
		evenementJournal ejTemporaire = new evenementJournal(); // Un element tampon
		ejTemporaire.Timestamp = System.currentTimeMillis(); // le timestamp
		ejTemporaire.type = EventType; // Le type d'event
		/*
		 * 	Type 0 = Undefined Event Type (or RAW)
		 *  Type 1 = PunchIn
		 *  Type 2 = PunchOut Event						
		 *  Type 3 = Comment Or Note Event
		 *  Type 4 = other
		 * 
		 */
		ejTemporaire.Data = Data;
		
		/*Chaque Evenement Journal est Unique (Theoriquement
		 * de cela, on leur assigne un ID � des fins pratiques
		 */
		if(JournaldeBord.isEmpty()){
			ejTemporaire.ID = 1; // donc si c'est le premier item dans la collection
		}
		else{
			ejTemporaire.ID = (JournaldeBord.get(JournaldeBord.size()-1).ID + 1); // sinon on trouve le dernier et on en ajoute un
		}
		
		//Ajout � la Collection
		JournaldeBord.add(ejTemporaire); 
	}
	
	/**
	*
	* @author Charles Perreault
	*
	* Retourne le nombre d'items dans la liste
	*/
	public int size(){
		int t;
		t = JournaldeBord.size();
		
		return t;
	}
	
	/**
	*
	* @author Charles Perreault
	*
	* 
	*/
	//Retourne vrai si existe --------------------------------TODO refactor to isNotNull
	public boolean exist()
	{
		boolean answer = false;
		if(size() > 0)
			answer = true;
		return answer;
		
	}
	
	/**
	*
	* @author Charles Perreault
	*
	* 
	*/
	//Retourne la DATE en String depuis un Long
	private String getDate(long time) {
	    Calendar cal = Calendar.getInstance(Locale.ENGLISH);
	    cal.setTimeInMillis(time);
	    String date = DateFormat.format("dd-MM-yyyy", cal).toString();
	    return date;
	}
	
	
	/**
	*
	* @author Charles Perreault, Anthony Pugliese
	*
	* Retourne le dernier �v�nement dans la liste selon le type entr� en param�tre
	*/
	public evenementJournal findLastEvent()
	{
		evenementJournal ejTemp = null;
		int dernierElement = JournaldeBord.size() -1 ;
			ejTemp = JournaldeBord.get(dernierElement);
		return ejTemp;
	}
	
	/**
	*
	* @author Charles Perreault, Anthony Pugliese
	*
	* S�rialise la liste d'�v�nement en format Json
	*/
	private String serialize(List<evenementJournal> listJDB)
	{
		
		Gson gson = new Gson();
		GsonBuilder builder = new GsonBuilder();
		String JournaldeBordEnJSON ="";
		/// Initialisation du s�rialiseur JSON
		
		JournaldeBordEnJSON = new GsonBuilder().create().toJson(listJDB);
		builder.setPrettyPrinting();
			gson = builder.create();
			JournaldeBordEnJSON = gson.toJson(listJDB);
		
		//S�rialisation

		return JournaldeBordEnJSON;
	}
	
	//LA S�rialisation
	/**
	*
	* @author Charles Perreault , Anthony Pugliese
	*
	* Sauvegarde sur l'appareil
	*/
	public boolean saveToDevice(Context ctx) 
	{
		//On r�cup�re ce qu'il faut �crire...
		String aSauvegarder = serialize(JournaldeBord);
		
		//On s'occupe des derniers pr�paratifs :
		String nomDuFichier = "JDBenJSON"; // Le nom du fichier
		File pathFichier = Path; // L'endroit o� on l'�crit
		
		//Moteur d'�criture
		FileOutputStream outputStream;
		try { // On s'essaye
			outputStream = ctx.openFileOutput(nomDuFichier, Context.MODE_PRIVATE); 
			outputStream.write(aSauvegarder.getBytes()); //on �crit le contenu de la string
			outputStream.close(); // fermeture du Fichier
		} catch (Exception e) {
			e.printStackTrace(); 	//Dans le cas d'une erreur quelquonque
			//outputStream.close();	// On s'assure de fermer le fichier
			return false; // en cas de probl�me on laisse savoir au autres m�thodes du programme une erreur
		}
		return true; // On retourne Vrai si il n'y a aucun probl�me et que on se rend jusqu'ici
	}

	/**
	*
	* @author Charles Perreault
	*
	* Retourne une liste d'evenements Journals
	*/
	public List<evenementJournal> loadFromDevice()
	{
		String _Name = "JDBenJSON"; // le m�me nom de fichier
		File _Path = Path; // le m�me path
		
		//une liste vide
		ArrayList<evenementJournal> JournaldeBord = new ArrayList<evenementJournal>();
		
		try{
		File serialisedObject = new File(_Path, _Name);
		
		if (!serialisedObject.exists()) {
			// TODO Retrouver pourquoi j'ai mis cte boutte de code l�...
		} else {
			FileReader fr = new FileReader(serialisedObject.getCanonicalFile());
			BufferedReader bufferedReader = new BufferedReader(fr);
			// Lecture du fichier
			String line;
			StringBuilder sb = new StringBuilder();
			while ((line = bufferedReader.readLine()) != null)
				sb.append(line);
			bufferedReader.close();
			String contenuDuFichier = sb.toString();
			// D�finition de la structure du fichier JSON
			Type typeJournal = new TypeToken<ArrayList<evenementJournal>>() {
			}.getType();
			// Cr�ation des instances à partir du fichier, groupés en une
			// Collection
			Gson gson = new Gson();
			
			JournaldeBord = gson.fromJson(contenuDuFichier, typeJournal);
			
			
		}
		} catch (IOException e) {
			e.printStackTrace();
			return JournaldeBord;
		}
		return JournaldeBord;
	}
}
