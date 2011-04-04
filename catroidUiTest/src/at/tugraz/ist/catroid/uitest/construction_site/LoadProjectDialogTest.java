package at.tugraz.ist.catroid.uitest.construction_site;

import java.io.File;
import java.io.IOException;

import android.content.pm.PackageManager.NameNotFoundException;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;
import android.widget.TextView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.brick.ComeToFrontBrick;
import at.tugraz.ist.catroid.content.brick.HideBrick;
import at.tugraz.ist.catroid.content.brick.PlaceAtBrick;
import at.tugraz.ist.catroid.content.brick.ScaleCostumeBrick;
import at.tugraz.ist.catroid.content.brick.ShowBrick;
import at.tugraz.ist.catroid.content.project.Project;
import at.tugraz.ist.catroid.content.script.Script;
import at.tugraz.ist.catroid.content.sprite.Sprite;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.utils.UtilFile;

import com.jayway.android.robotium.solo.Solo;

public class LoadProjectDialogTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
    private Solo solo;
    private String testProject2 = "testProject2";

    public LoadProjectDialogTest() {
        super("at.tugraz.ist.catroid.ui", MainMenuActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        solo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    public void tearDown() throws Exception {

        File directory = new File("/sdcard/catroid/" + testProject2);
        UtilFile.deleteDirectory(directory);
        assertFalse("testProject was not deleted!", directory.exists());

        try {
            solo.finalize();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        getActivity().finish();

        super.tearDown();

    }

    public void testLoadProjectDialog() throws NameNotFoundException, IOException {
        createTestProject(testProject2);
        solo.clickOnButton(getActivity().getString(R.string.load_project));
        solo.clickOnText(testProject2);

        ListView spritesList = (ListView) solo.getCurrentActivity().findViewById(R.id.spriteListView);
        Sprite first = (Sprite) spritesList.getItemAtPosition(1);
        assertEquals("Sprite at index 1 is not \"cat\"!", "cat", first.getName());
        Sprite second = (Sprite) spritesList.getItemAtPosition(2);
        assertEquals("Sprite at index 2 is not \"dog\"!", "dog", second.getName());
        Sprite third = (Sprite) spritesList.getItemAtPosition(3);
        assertEquals("Sprite at index 3 is not \"horse\"!", "horse", third.getName());
        Sprite fourth = (Sprite) spritesList.getItemAtPosition(4);
        assertEquals("Sprite at index 4 is not \"pig\"!", "pig", fourth.getName());

        solo.goBack();

        TextView currentProject = (TextView) getActivity().findViewById(R.id.currentProjectNameTextView);

        assertEquals("Current project is not testProject2!", getActivity().getString(R.string.current_project) + " "
                + testProject2, currentProject.getText());

    }

    public void createTestProject(String projectName) throws IOException, NameNotFoundException {
        StorageHandler storageHandler = StorageHandler.getInstance();

        int xPosition = 457;
        int yPosition = 598;
        double scaleValue = 0.8;

        Project project = new Project(getActivity(), projectName);
        Sprite firstSprite = new Sprite("cat");
        Sprite secondSprite = new Sprite("dog");
        Sprite thirdSprite = new Sprite("horse");
        Sprite fourthSprite = new Sprite("pig");
        Script testScript = new Script();
        Script otherScript = new Script();
        HideBrick hideBrick = new HideBrick(firstSprite);
        ShowBrick showBrick = new ShowBrick(firstSprite);
        ScaleCostumeBrick scaleCostumeBrick = new ScaleCostumeBrick(secondSprite, scaleValue);
        ComeToFrontBrick comeToFrontBrick = new ComeToFrontBrick(firstSprite);
        PlaceAtBrick placeAtBrick = new PlaceAtBrick(secondSprite, xPosition, yPosition);

        // adding Bricks: ----------------
        testScript.addBrick(hideBrick);
        testScript.addBrick(showBrick);
        testScript.addBrick(scaleCostumeBrick);
        testScript.addBrick(comeToFrontBrick);

        otherScript.addBrick(placeAtBrick); // secondSprite
        otherScript.setPaused(true);
        // -------------------------------

        firstSprite.getScriptList().add(testScript);
        secondSprite.getScriptList().add(otherScript);

        project.addSprite(firstSprite);
        project.addSprite(secondSprite);
        project.addSprite(thirdSprite);
        project.addSprite(fourthSprite);

        storageHandler.saveProject(project);
    }
}