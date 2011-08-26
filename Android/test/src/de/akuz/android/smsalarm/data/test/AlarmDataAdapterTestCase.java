package de.akuz.android.smsalarm.data.test;

import java.util.List;

import junit.framework.Assert;

import de.akuz.android.smsalarm.data.AlarmDataAdapter;
import de.akuz.android.smsalarm.data.AlarmGroup;
import android.content.Context;
import android.test.AndroidTestCase;

public class AlarmDataAdapterTestCase extends AndroidTestCase {
	
	private Context mContext;
	private AlarmDataAdapter testAdapter;
	
	private final static String TEST_NAME="Testgruppe";
	private final static String TEST_KEYWORD="bhp1";
	private final static String TEST_SENDER="juh_do";

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.mContext = getContext();
		this.testAdapter = AlarmDataAdapter.getInstance(mContext);
		testAdapter.open();
		testAdapter.clear();
	}

	@Override
	protected void tearDown() throws Exception {
		testAdapter.closeAllChilds();
		testAdapter.close();
		super.tearDown();
	}
	
	private AlarmGroup createAlarmGroup(String name, String keyword, String[] numbers){
		AlarmGroup group = testAdapter.createNewAlarmGroup(name, keyword);
		for(int i=0;i<numbers.length;i++){
			group.addAllowedNumber(numbers[i]);
		}
		return group;
	}

	public void testSameInstanceForSameContext() throws Exception{
		AlarmDataAdapter newReference = AlarmDataAdapter.getInstance(mContext);
		Assert.assertEquals(newReference, testAdapter);
	}

	public void testCreatingNewAlarmGroupAndAddingAllowedNumber() throws Exception {
		AlarmGroup group = createAlarmGroup(TEST_NAME,
				TEST_KEYWORD, new String[]{TEST_SENDER});
		Assert.assertEquals(1,testAdapter.getAllAlarmGroups().size());
		Assert.assertEquals(1, group.getAllowedNumbers().size());
		Assert.assertEquals(TEST_SENDER, group.getAllowedNumbers().get(0));
	}
	
	public void testRetrieveGroupByNumberAndKeyword() throws Exception {
		AlarmGroup group = createAlarmGroup(TEST_NAME,
				TEST_KEYWORD, new String[]{TEST_SENDER});
		group = 
			testAdapter.getAlarmGroupByNumberAndKeyword(TEST_SENDER, TEST_KEYWORD);
		Assert.assertNotNull(group);
		Assert.assertEquals(TEST_KEYWORD, group.getKeyword());
		Assert.assertEquals(TEST_NAME, group.getName());
		
		AlarmGroup nullGroup = 
			testAdapter.getAlarmGroupByNumberAndKeyword(
					"fake_number", "nonexistant keyword");
		Assert.assertTrue(nullGroup == null);
	}
	
	public void testGetAlarmGroupById() throws Exception {
		AlarmGroup group = createAlarmGroup(TEST_NAME,
				TEST_KEYWORD, new String[]{TEST_SENDER});
		AlarmGroup group2 = testAdapter.getAlarmGroupById(group.getId());
		Assert.assertEquals(group.getId(), group2.getId());
		Assert.assertEquals(group, group2);
		//now test for non cache hits
		long id = group.getId();
		testAdapter.closeAllChilds();
		group2 = testAdapter.getAlarmGroupById(id);
		Assert.assertNotNull(group2);
		Assert.assertEquals(id, group2.getId());
		Assert.assertEquals(TEST_NAME, group2.getName());
		Assert.assertEquals(TEST_KEYWORD, group2.getKeyword());
		Assert.assertEquals(1, group2.getAllowedNumbers().size());
		Assert.assertEquals(TEST_SENDER, group2.getAllowedNumbers().get(0));
	}
	
	public void testRemoveAlarmGroup() throws Exception {
		AlarmGroup group = createAlarmGroup(TEST_NAME,
				TEST_KEYWORD, new String[]{TEST_SENDER});
		Assert.assertEquals(1, testAdapter.getAllAlarmGroups().size());
		testAdapter.removeAlarmGroup(group.getId());
		Assert.assertEquals(0, testAdapter.getAllAlarmGroups().size());
	}
	
	public void testGetAlarmGroupsByNumber() throws Exception {
		AlarmGroup group = createAlarmGroup(TEST_NAME,
				TEST_KEYWORD, new String[]{TEST_SENDER});
		List<AlarmGroup> testList = testAdapter.getAlarmGroupsByNumber(TEST_SENDER);
		Assert.assertEquals(1, testList.size());
		Assert.assertEquals(group, testList.get(0));
		//And no again wit no groups in cache
		testAdapter.closeAllChilds();
		testList = testAdapter.getAlarmGroupsByNumber(TEST_SENDER);
		Assert.assertNotNull(testList);
		Assert.assertEquals(1, testList.size());
		Assert.assertEquals(group.getId(), testList.get(0).getId());
		//Test with mobile number
		String number = "01791791798";
		group.addAllowedNumber(number);
		testList = testAdapter.getAlarmGroupsByNumber(number);
		Assert.assertEquals(1, testList.size());
		Assert.assertEquals(group.getId(), testList.get(0).getId());
	}
	
	public void testGetAlarmGroupByKeyword() throws Exception {
		AlarmGroup group = createAlarmGroup(TEST_NAME,
				TEST_KEYWORD, new String[]{TEST_SENDER});
		AlarmGroup group2 = testAdapter.getAlarmGroupByKeyword(TEST_KEYWORD);
		Assert.assertNotNull(group2);
		Assert.assertEquals(group.getId(), group2.getId());
	}

}
