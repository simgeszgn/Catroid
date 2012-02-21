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
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.ChangeGhostEffectBrick;
import at.tugraz.ist.catroid.ui.ScriptActivity;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;
import at.tugraz.ist.catroid.utils.Utils;

import com.jayway.android.robotium.solo.Solo;

public class ChangeGhostEffectTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {
	private Solo solo;
	private Project project;
	private ChangeGhostEffectBrick changeGhostEffectBrick;
	private double effectToChange;

	public ChangeGhostEffectTest() {
		super("at.tugraz.ist.catroid", ScriptTabActivity.class);
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
	public void testChangeGhostEffectBrick() {
		int childrenCount = ((ScriptActivity) getActivity().getCurrentActivity()).getAdapter()
				.getChildCountFromLastGroup();
		int groupCount = ((ScriptActivity) getActivity().getCurrentActivity()).getAdapter().getGroupCount();

		assertEquals("Incorrect number of bricks.", 2, solo.getCurrentListViews().get(0).getChildCount());
		assertEquals("Incorrect number of bricks.", 1, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());

		assertEquals("Wrong Brick instance.", projectBrickList.get(0), ((ScriptActivity) getActivity()
				.getCurrentActivity()).getAdapter().getChild(groupCount - 1, 0));
		assertNotNull("TextView does not exist",
				solo.getText(getActivity().getString(R.string.brick_change_ghost_effect)));

		solo.clickOnEditText(0);
		solo.clearEditText(0);
		solo.enterText(0, effectToChange + "");
		solo.goBack();
		solo.clickOnButton(0);

		solo.sleep(1000);

		assertEquals("Wrong text in field", effectToChange, changeGhostEffectBrick.getChangeGhostEffect());
		assertEquals("Text not updated", effectToChange, Double.parseDouble(solo.getEditText(0).getText().toString()));
	}

	public void testResizeInputField() {
		UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_home);
		solo.sleep(200);
		solo.clickOnText(getActivity().getString(R.string.current_project_button));
		createProject();
		solo.clickOnText(solo.getCurrentListViews().get(0).getItemAtPosition(0).toString());
		solo.sleep(100);

		double[] changeBrightnessTestValues = new double[] { 1.0, 100.55, -0.1 };
		double currentChangeBrightnessValue = 0.0;
		int editTextWidth = 0;
		for (int i = 0; i < changeBrightnessTestValues.length; i++) {
			currentChangeBrightnessValue = changeBrightnessTestValues[i];
			UiTestUtils.insertDoubleIntoEditText(solo, 0, currentChangeBrightnessValue);
			solo.clickOnButton(0);
			solo.sleep(100);
			assertTrue("EditText not resized - value not (fully) visible",
					solo.searchText(currentChangeBrightnessValue + ""));
			editTextWidth = solo.getEditText(0).getWidth();
			assertTrue("Minwidth of EditText should be 60 dpi",
					editTextWidth >= Utils.getPhysicalPixels(60, solo.getCurrentActivity().getBaseContext()));
		}

		solo.sleep(200);
		currentChangeBrightnessValue = 1000.55;
		UiTestUtils.insertDoubleIntoEditText(solo, 0, currentChangeBrightnessValue);
		solo.clickOnButton(0);
		solo.sleep(100);
		assertFalse("Number too long - should not be resized and fully visible",
				solo.searchText(currentChangeBrightnessValue + ""));
	}

	private void createProject() {
		effectToChange = 11.2;
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript(sprite);
		changeGhostEffectBrick = new ChangeGhostEffectBrick(sprite, 30.5);
		script.addBrick(changeGhostEffectBrick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}
