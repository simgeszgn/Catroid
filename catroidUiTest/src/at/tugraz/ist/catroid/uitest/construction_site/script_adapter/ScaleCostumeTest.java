package at.tugraz.ist.catroid.uitest.construction_site.script_adapter;

import java.util.ArrayList;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.constructionSite.content.ProjectManager;
import at.tugraz.ist.catroid.content.brick.Brick;
import at.tugraz.ist.catroid.content.brick.GoNStepsBackBrick;
import at.tugraz.ist.catroid.content.brick.HideBrick;
import at.tugraz.ist.catroid.content.brick.IfTouchedBrick;
import at.tugraz.ist.catroid.content.brick.PlaceAtBrick;
import at.tugraz.ist.catroid.content.brick.PlaySoundBrick;
import at.tugraz.ist.catroid.content.brick.ScaleCostumeBrick;
import at.tugraz.ist.catroid.content.project.Project;
import at.tugraz.ist.catroid.content.script.Script;
import at.tugraz.ist.catroid.content.sprite.Sprite;
import at.tugraz.ist.catroid.ui.ScriptActivity;

import com.jayway.android.robotium.solo.Solo;

/**
 * 
 * @author Daniel Burtscher
 *
 */
public class ScaleCostumeTest extends ActivityInstrumentationTestCase2<ScriptActivity>{
	private Solo solo;
	private Project project;
	private ScaleCostumeBrick scaleCostumeBrick;

	public ScaleCostumeTest() {
		super("at.tugraz.ist.catroid",
				ScriptActivity.class);
	}
	
	@Override
    public void setUp() throws Exception {
		createProject();
		solo = new Solo(getInstrumentation(), getActivity());
	}
	
	@Override
    public void tearDown() throws Exception {	
		try {	
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		getActivity().finish();
		super.tearDown();
	}
	
	@Smoke
	public void testScaleCostumeBrick() throws Throwable {
		int childrenCount = getActivity().getAdapter().getChildCountFromLastGroup();
		int groupCount = getActivity().getAdapter().getGroupCount();
		assertEquals("Incorrect number of bricks.", 2, solo.getCurrentListViews().get(0).getChildCount());
		assertEquals("Incorrect number of bricks.", 1, childrenCount);
		
		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScriptList().get(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());
		
		assertEquals("Wrong Brick instance.", projectBrickList.get(0), getActivity().getAdapter().getChild(groupCount-1, 0));
		assertNotNull("TextView does not exist", solo.getText(getActivity().getString(R.string.scaleCustome)));
		
		double newScale = 25;
		
		solo.clickOnEditText(0);
		solo.clearEditText(0);
		solo.enterText(0, newScale + "");
		solo.clickOnButton(0);
		
		Thread.sleep(1000);
		assertEquals("Wrong text in field", newScale, scaleCostumeBrick.getScale());
		assertEquals("Text not updated", newScale, Double.parseDouble(solo.getEditText(0).getText().toString()));
	}
	
	private void createProject() {
		project = new Project(null, "testProject");
        Sprite sprite = new Sprite("cat");
        Script script = new Script(); 
        scaleCostumeBrick = new ScaleCostumeBrick(sprite, 20);
        script.addBrick(scaleCostumeBrick);
        
        sprite.getScriptList().add(script);
        project.addSprite(sprite);
        
        ProjectManager.getInstance().setProject(project);
        ProjectManager.getInstance().setCurrentSprite(sprite);
        ProjectManager.getInstance().setCurrentScript(script);
	}
	
}