package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 1)
 *
 * @author Olivier Liechti
 */
public class RouletteV2LionelWidmerTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

    @Test
    @TestAuthor(githubId = "lionelwidmer")
    public void theTestRouletteServerShouldRunDuringTests() throws IOException {
        assertTrue(roulettePair.getServer().isRunning());
    }

    @Test
    @TestAuthor(githubId = "lionelwidmer")
    public void theTestRouletteClientShouldBeConnectedWhenATestStarts() throws IOException {
        assertTrue(roulettePair.getClient().isConnected());
    }

    @Test
    @TestAuthor(githubId = "lionelwidmer")
    public void itShouldBePossibleForARouletteClientToConnectToARouletteServer() throws Exception {
        int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = new RouletteV2ClientImpl();
        assertFalse(client.isConnected());
        client.connect("localhost", port);
        assertTrue(client.isConnected());
    }

    @Test
    @TestAuthor(githubId = "lionelwidmer")
    public void theServerShouldReturnTheCorrectVersionNumber() throws IOException {
        assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
    }

    @Test
    @TestAuthor(githubId = "lionelwidmer")
    public void theServerShouldHaveZeroStudentsAtStart() throws IOException {
        int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = new RouletteV2ClientImpl();
        client.connect("localhost", port);
        int numberOfStudents = client.getNumberOfStudents();
        assertEquals(0, numberOfStudents);
    }

    @Test
    @TestAuthor(githubId = {"lionelwidmer", "SoftEng-HEIGVD"})
    public void theServerShouldStillHaveZeroStudentsAtStart() throws IOException {
        assertEquals(0, roulettePair.getClient().getNumberOfStudents());
    }

    @Test
    @TestAuthor(githubId = "SoftEng-HEIGVD")
    public void theServerShouldCountStudents() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
        assertEquals(0, client.getNumberOfStudents());
        client.loadStudent("sacha");
        assertEquals(1, client.getNumberOfStudents());
        client.loadStudent("olivier");
        assertEquals(2, client.getNumberOfStudents());
        client.loadStudent("fabienne");
        assertEquals(3, client.getNumberOfStudents());
    }

    @Test
    @TestAuthor(githubId = "lionelwidmer")
    public void theServerShouldSendAnErrorResponseWhenRandomIsCalledAndThereIsNoStudent() throws IOException, EmptyStoreException {
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
        exception.expect(EmptyStoreException.class);
        client.pickRandomStudent();
    }

    @Test
    @TestAuthor(githubId = "lionelwidmer")
    public void theServerShouldClearStudentStoreWhenClerFunctionIsInvolved() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
        client.loadStudent("gilbert");
        client.loadStudent("albert");
        client.clearDataStore();
        assertEquals(0, client.getNumberOfStudents());
    }

    @Test
    @TestAuthor(githubId = "lionelwidmer")
    public void theServerShouldReturnListOfStudents() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
        client.loadStudent("gilbert");
        client.loadStudent("gilbert2");
        List<Student> students = client.listStudents();
        assertEquals(2, students.size());
        assertTrue(students.get(0).getFullname().contains("gilbert"));
    }

    @Test
    @TestAuthor(githubId = "lionelwidmer")
    public void itShouldBePossibleToLoadMultipleStudents() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
        client.loadStudent("gilbert");
        client.loadStudent("gilbert2");
        List<Student> students = client.listStudents();
        client.clearDataStore();
        client.loadStudents(students);
        assertEquals(2, client.getNumberOfStudents());
    }
}