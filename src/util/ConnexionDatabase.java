package util;


import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnexionDatabase {
  private static final String PROPERTIES_FILE = "src/resources/database.properties";
  private static final Properties props = new Properties();

  static {
      try (FileInputStream input = new FileInputStream(PROPERTIES_FILE)){
          props.load(input);
      } catch (IOException e){
          throw new RuntimeException("Impossible de charge le fichier de properties !");
      }
  }

  private static String URL = props.getProperty("db.url");
  private static String USERNAME = props.getProperty("db.username");
  private static String PASSWORD = props.getProperty("db.password");

  public  static Connection getConnection() throws SQLException{
      try{
          if (URL == null || USERNAME == null || PASSWORD == null){
              throw new IllegalStateException("les proprietes de connexion (db.url, db.username, db.password) doit etre define dans le fichier database.properties");
          }
          return DriverManager.getConnection(URL,USERNAME,PASSWORD);
      }catch (SQLException e){
          throw  new SQLException("Erreur lors de la connexion a la base :" + e.getMessage(),e);
      }
  }

  public static boolean testConnection(){
      try (Connection conn = getConnection()){
          return conn.isValid(2);
      }catch (SQLException e){
          System.err.println("Test de connexion echoue : " + e.getMessage());
          return  false;
      }
  }
}
