package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import java.io.IOException;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 1)
 *
 * @author Olivier Liechti
 */
public class RouletteV2Callisto88Test {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Rule
  public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);


  @Test
  @TestAuthor(githubId = "Callisto88")
  public void theServerShouldClearDataStore() throws IOException {
    int port = roulettePair.getServer().getPort();
    IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
    client.connect("localhost", port);
    client.clearDataStore();
    assertEquals(0, client.getNumberOfStudents());
  }
  
  @Test
  @TestAuthor(githubId = "Callisto88")
  public void theServerShouldReturnTheListOfStudents() throws IOException {
      int port = roulettePair.getServer().getPort();
      
      IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
      client.connect("localhost", port);
      List<Student> listOfStudents = client.listStudents();
      
      if(!listOfStudents.isEmpty()){
          for(Student s : listOfStudents){
              s.getFullname();
          }
      }else{
          System.out.println("La liste est vide.");
      }
  }
  
  @Test
  @TestAuthor(githubId = "Callisto88")
  public void testDefaultPortNumber() throws IOException {
      int port = RouletteV2Protocol.DEFAULT_PORT;
      
      assertEquals(2613, port);
  }
          
}