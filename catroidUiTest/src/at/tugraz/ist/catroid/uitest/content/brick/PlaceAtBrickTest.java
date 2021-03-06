/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.uitest.content.brick;

import java.util.ArrayList;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.SoundInfo;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.HideBrick;
import at.tugraz.ist.catroid.content.bricks.PlaceAtBrick;
import at.tugraz.ist.catroid.content.bricks.PlaySoundBrick;
import at.tugraz.ist.catroid.content.bricks.SetSizeToBrick;
import at.tugraz.ist.catroid.ui.ScriptActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

/**
 * 
 * @author Daniel Burtscher
 * 
 */
public class PlaceAtBrickTest extends ActivityInstrumentationTestCase2<ScriptActivity> {
	private Solo solo;
	private Project project;
	private PlaceAtBrick placeAtBrick;

	public PlaceAtBrickTest() {
		super("at.tugraz.ist.catroid", ScriptActivity.class);
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
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
	}

	@Smoke
	public void testPlaceAtBrick() throws InterruptedException {
		int childrenCount = getActivity().getAdapter().getChildCountFromLastGroup();
		int groupCount = getActivity().getAdapter().getGroupCount();

		assertEquals("Incorrect number of bricks.", 5, solo.getCurrentListViews().get(0).getChildCount());
		assertEquals("Incorrect number of bricks.", 4, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 4, projectBrickList.size());

		assertEquals("Wrong Brick instance.", projectBrickList.get(0),
				getActivity().getAdapter().getChild(groupCount - 1, 0));

		assertEquals("Wrong Brick instance.", projectBrickList.get(1),
				getActivity().getAdapter().getChild(groupCount - 1, 1));

		assertEquals("Wrong Brick instance.", projectBrickList.get(2),
				getActivity().getAdapter().getChild(groupCount - 1, 2));

		assertEquals("Wrong Brick instance.", projectBrickList.get(3),
				getActivity().getAdapter().getChild(groupCount - 1, 3));
		assertNotNull("TextView does not exist", solo.getText(getActivity().getString(R.string.brick_place_at)));

		int xPosition = 987;
		int yPosition = 654;

		solo.clickOnEditText(0);
		solo.clearEditText(0);
		solo.enterText(0, xPosition + "");
		solo.goBack();
		solo.sleep(300);
		solo.clickOnButton(0);

		solo.sleep(300);
		int actualXPosition = (Integer) UiTestUtils.getPrivateField("xPosition", placeAtBrick);
		assertEquals("Text not updated", xPosition + "", solo.getEditText(0).getText().toString());
		assertEquals("Value in Brick is not updated", xPosition, actualXPosition);

		solo.clickOnEditText(1);
		solo.clearEditText(0);
		solo.enterText(0, yPosition + "");
		solo.goBack();
		solo.clickOnButton(0);

		solo.sleep(300);
		int actualYPosition = (Integer) UiTestUtils.getPrivateField("yPosition", placeAtBrick);
		assertEquals("Text not updated", yPosition + "", solo.getEditText(1).getText().toString());
		assertEquals("Value in Brick is not updated", yPosition, actualYPosition);
	}

	public void testResizeInputFields() {
		ProjectManager.getInstance().deleteCurrentProject();
		createTestProject();

		for (int i = 0; i < 2; i++) {
			UiTestUtils.testIntegerEditText(solo, i, 1, 60, true);
			UiTestUtils.testIntegerEditText(solo, i, 12345, 60, true);
			UiTestUtils.testIntegerEditText(solo, i, -1, 60, true);
			UiTestUtils.testIntegerEditText(solo, i, 123456, 60, false);
		}
	}

	private void createProject() {
		project = new Project(null, "testProject");
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript(sprite);
		script.addBrick(new HideBrick(sprite));
		placeAtBrick = new PlaceAtBrick(sprite, 105, 206);
		script.addBrick(placeAtBrick);
		PlaySoundBrick soundBrick = new PlaySoundBrick(sprite);
		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setSoundFileName("sound.mp3");
		soundInfo.setTitle("sound.mp3");
		soundBrick.setSoundInfo(soundInfo);
		script.addBrick(soundBrick);

		script.addBrick(new SetSizeToBrick(sprite, 80));

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}

	private void createTestProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript(sprite);
		placeAtBrick = new PlaceAtBrick(sprite, 0, 0);
		script.addBrick(placeAtBrick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}

}
