package com.ipiecoles.java.java230;

import com.ipiecoles.java.java230.exceptions.BatchException;
import com.ipiecoles.java.java230.model.Commercial;
import com.ipiecoles.java.java230.model.Employe;
import com.ipiecoles.java.java230.model.Manager;
import com.ipiecoles.java.java230.model.Technicien;
import com.ipiecoles.java.java230.repository.EmployeRepository;
import com.ipiecoles.java.java230.repository.ManagerRepository;
import org.hibernate.engine.jdbc.batch.spi.Batch;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class MyRunner implements CommandLineRunner {

    private static final String REGEX_TYPE = "^[MTC]{1}.*";
    private static final String REGEX_MATRICULE = "^[MTC][0-9]{5}$";
    private static final String REGEX_NOM = ".*";
    private static final String REGEX_PRENOM = ".*";
    private static final int NB_CHAMPS_MANAGER = 5;
    private static final int NB_CHAMPS_TECHNICIEN = 7;
    private static final String REGEX_MATRICULE_MANAGER = "^M[0-9]{5}$";
    private static final int NB_CHAMPS_COMMERCIAL = 7;
    private static final String REGEX_GRADE_TECHNICIEN = "[1-5]{1}$";

    @Autowired
    private EmployeRepository employeRepository;

    @Autowired
    private ManagerRepository managerRepository;

    private List<Employe> employes = new ArrayList<Employe>();

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void run(String... strings) throws Exception {
        String fileName = "employes.csv";
        readFile(fileName);
        //readFile(strings[0]);
    }

    /**
     * Méthode qui lit le fichier CSV en paramètre afin d'intégrer son contenu en BDD
     * @param fileName Le nom du fichier (à mettre dans src/main/resources)
     * @return une liste contenant les employés à insérer en BDD ou null si le fichier n'a pas pu être le
     */
    public List<Employe> readFile(String fileName) throws Exception {
        Stream<String> stream;
        stream = Files.lines(Paths.get(new ClassPathResource(fileName).getURI()));
        //TODO
        Integer i = 0;
        for (String ligne : stream.collect(Collectors.toList())){
            i++;
            try {
                processLine(ligne);
            }catch (BatchException e){
                System.out.println("Ligne "+ i + " : " + e.getMessage()+ " => " + ligne  );
            }
        }

        return employes;
    }

    /**
     * Méthode qui regarde le premier caractère de la ligne et appelle la bonne méthode de création d'employé
     * @param ligne la ligne à analyser
     * @throws BatchException si le type d'employé n'a pas été reconnu
     */
    private void processLine(String ligne) throws BatchException {
        //TODO
        if (!ligne.matches("^[MCT]{1}.*")){
            throw new BatchException("Type d'employé inconnu : " + ligne.charAt(0));
        }
        String[] tab = ligne.split(",");
        if (!tab[0].matches(REGEX_MATRICULE)){
            throw new BatchException("La chaîne "+ tab[0] + " ne respecte pas l'expression régulière "+REGEX_MATRICULE);
        }
        switch(ligne.charAt(0)){
            case 'T' :
                processTechnicien(ligne);
                break;
            case 'C' :
                processCommercial(ligne);
                break;
            case 'M' :
                processManager(ligne);
                break;
        }
        try {
            LocalDate d = DateTimeFormat.forPattern("dd/MM/yyyy").parseLocalDate(tab[3]);

        } catch (Exception e) {
            throw new BatchException(tab[3] + " ne respecte pas le format de date dd/MM/yyyy");
        }
    }

    /**
     * Méthode qui crée un Commercial à partir d'une ligne contenant les informations d'un commercial et l'ajoute dans la liste globale des employés
     * @param ligneCommercial la ligne contenant les infos du commercial à intégrer
     * @throws BatchException s'il y a un problème sur cette ligne
     */
    private void processCommercial(String ligneCommercial) throws BatchException {
        //TODO
        String[] tabCommercial = ligneCommercial.split(",");
        Integer nbElement = 0;
        nbElement = tabCommercial.length;
        if (nbElement>NB_CHAMPS_COMMERCIAL||nbElement<NB_CHAMPS_COMMERCIAL){
            throw new BatchException("La ligne Commercial ne contient pas "+NB_CHAMPS_COMMERCIAL+" éléments mais " + nbElement);
        }
        else if (!tabCommercial[1].matches(REGEX_NOM)||!tabCommercial[2].matches(REGEX_PRENOM)){
            throw new BatchException("Le nom ou le prenom ne respect pas les règles: " + tabCommercial[1] +" , "+ tabCommercial[2]);
        }
        try {
            Double.parseDouble(tabCommercial[4]);
            //System.out.println(tabTechnicien[4]);
        } catch (Exception e){
            throw  new BatchException(tabCommercial[4] +" n'est pas un nombre valide pour un salaire");
        }
        try {
            Double.parseDouble(tabCommercial[5]);
        }catch (Exception e){
            throw new BatchException("Le chiffre d'affaire du commercial est incorrect : " + tabCommercial[5]);
        }
        try {
            Integer.parseInt(tabCommercial[6]);
        }catch (Exception e){
            throw new BatchException("La performance du commercial est incorrecte : " + tabCommercial[6]);
        }

    }

    /**
     * Méthode qui crée un Manager à partir d'une ligne contenant les informations d'un manager et l'ajoute dans la liste globale des employés
     * @param ligneManager la ligne contenant les infos du manager à intégrer
     * @throws BatchException s'il y a un problème sur cette ligne
     */
    private void processManager(String ligneManager) throws BatchException {
        //TODO
        String[] tabManager = ligneManager.split(",");
        Integer nbElement = 0;
        nbElement = tabManager.length;
        if (nbElement>NB_CHAMPS_MANAGER||nbElement<NB_CHAMPS_MANAGER){
            throw new BatchException("La ligne manager ne contient pas "+NB_CHAMPS_MANAGER+" éléments mais " + nbElement);
        }
        else if (!tabManager[1].matches(REGEX_NOM)||!tabManager[2].matches(REGEX_PRENOM)){
            throw new BatchException("Le nom ou le prenom ne respect pas les règles: " + tabManager[1] +" , "+ tabManager[2]);
        }
        try {
            Double.parseDouble(tabManager[4]);
            //System.out.println(tabTechnicien[4]);
        } catch (Exception e){
            throw  new BatchException(tabManager[4] +" n'est pas un nombre valide pour un salaire");
        }

    }

    /**
     * Méthode qui crée un Technicien à partir d'une ligne contenant les informations d'un technicien et l'ajoute dans la liste globale des employés
     * @param ligneTechnicien la ligne contenant les infos du technicien à intégrer
     * @throws BatchException s'il y a un problème sur cette ligne
     */
    private void processTechnicien(String ligneTechnicien) throws BatchException {
        //TODO 4
        String[] tabTechnicien = ligneTechnicien.split(",");
        Integer nbElement = 0;
        nbElement = tabTechnicien.length;
        if (nbElement>NB_CHAMPS_TECHNICIEN||nbElement<NB_CHAMPS_TECHNICIEN){
            throw new BatchException("La ligne Technicien ne contient pas "+NB_CHAMPS_TECHNICIEN+" éléments mais " + nbElement);
        }
        else if (!tabTechnicien[1].matches(REGEX_NOM)||!tabTechnicien[2].matches(REGEX_PRENOM)){
            throw new BatchException("Le nom ou le prenom ne respect pas les règles: " + tabTechnicien[1] +" , "+ tabTechnicien[2]);
        }
        try {
            Double.parseDouble(tabTechnicien[4]);
            //System.out.println(tabTechnicien[4]);
        } catch (Exception e){
            throw  new BatchException(tabTechnicien[4] +" n'est pas un nombre valide pour un salaire");
        }
        if (!tabTechnicien[5].matches(REGEX_GRADE_TECHNICIEN)){
            throw new BatchException("Le grade doit être compris entre 1 et 5 : " +tabTechnicien[5]+ ", technicien : " +ligneTechnicien.charAt(0));
        }
        if (!tabTechnicien[6].matches(REGEX_MATRICULE_MANAGER)){
            throw new BatchException("la chaîne "+tabTechnicien[6]+" ne respecte pas l'expression régulière "+ REGEX_MATRICULE_MANAGER);
        }
        try {
            LocalDate d = DateTimeFormat.forPattern("dd/MM/yyyy").parseLocalDate(tabTechnicien[3]);

        } catch (Exception e) {
            throw new BatchException(tabTechnicien[3] + " ne respecte pas le format de date dd/MM/yyyy");
        }
        Employe m = employeRepository.findByMatricule(tabTechnicien[6]);
        if (m==null){
            throw new BatchException("Le manager de matricule "+tabTechnicien[6]+" n'a pas été trouvé dans le fichier ou en base de données");
        }
        // Le manager de matricule M99999 n'a pas été trouvé dans le fichier ou en base de données
    }

}
