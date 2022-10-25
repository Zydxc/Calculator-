package com.halitcan.calc;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = true;
	public static final boolean includeTitle = false;
    public static WeakReference<Activity> previousOne;
    public static boolean dontPause;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mostCurrent = this;
		if (processBA == null) {
			processBA = new anywheresoftware.b4a.ShellBA(this.getApplicationContext(), null, null, "com.halitcan.calc", "com.halitcan.calc.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
				p.finish();
			}
		}
        processBA.setActivityPaused(true);
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(this, processBA, wl, false))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "com.halitcan.calc", "com.halitcan.calc.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "com.halitcan.calc.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEventFromUI(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return main.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeydown", this, new Object[] {keyCode, event}))
            return true;
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeyup", this, new Object[] {keyCode, event}))
            return true;
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null)
            return;
        if (this != mostCurrent)
			return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        if (!dontPause)
            BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        else
            BA.LogInfo("** Activity (main) Pause event (activity is not paused). **");
        if (mostCurrent != null)
            processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        if (!dontPause) {
            processBA.setActivityPaused(true);
            mostCurrent = null;
        }

        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
            main mc = mostCurrent;
			if (mc == null || mc != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
            if (mc != mostCurrent)
                return;
		    processBA.raiseEvent(mc._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        for (int i = 0;i < permissions.length;i++) {
            Object[] o = new Object[] {permissions[i], grantResults[i] == 0};
            processBA.raiseEventFromDifferentThread(null,null, 0, "activity_permissionresult", true, o);
        }
            
    }



public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        b4a.example.dateutils._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}
public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
return vis;}

private static BA killProgramHelper(BA ba) {
    if (ba == null)
        return null;
    anywheresoftware.b4a.BA.SharedProcessBA sharedProcessBA = ba.sharedProcessBA;
    if (sharedProcessBA == null || sharedProcessBA.activityBA == null)
        return null;
    return sharedProcessBA.activityBA.get();
}
public static void killProgram() {
     {
            Activity __a = null;
            if (main.previousOne != null) {
				__a = main.previousOne.get();
			}
            else {
                BA ba = killProgramHelper(main.mostCurrent == null ? null : main.mostCurrent.processBA);
                if (ba != null) __a = ba.activity;
            }
            if (__a != null)
				__a.finish();}

BA.applicationContext.stopService(new android.content.Intent(BA.applicationContext, starter.class));
}
public static class _typestate{
public boolean IsInitialized;
public String Name;
public int[] state2;
public int state1;
public void Initialize() {
IsInitialized = true;
Name = "";
state2 = new int[2];
;
state1 = 0;
}
@Override
		public String toString() {
			return BA.TypeToString(this, false);
		}}
public anywheresoftware.b4a.keywords.Common __c = null;
public de.donmanfred.ResourceHelper _res = null;
public static String _strclearmemory = "";
public static String _strcallmemory = "";
public static String _strsavedtomem = "";
public static String _strmemclerared = "";
public static int _stage1 = 0;
public static int _stage2 = 0;
public static int _stage3 = 0;
public static int _stage_result = 0;
public static int _operation_sum = 0;
public static int _operation_subtract = 0;
public static int _operation_multiply = 0;
public static int _operation_division = 0;
public static int _operation_karekok = 0;
public static int _operation_undefined = 0;
public static String _lang = "";
public static int _skin = 0;
public static double _num1 = 0;
public static double _num2 = 0;
public static double _numresult = 0;
public static double _mem = 0;
public static int _stage = 0;
public static int _operation = 0;
public static boolean _first = false;
public static int _ox = 0;
public static int _oy = 0;
public anywheresoftware.b4a.objects.ButtonWrapper _button0 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button1 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button2 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button3 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button4 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button5 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button6 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button7 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button8 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button9 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _buttonadd = null;
public anywheresoftware.b4a.objects.ButtonWrapper _buttonback = null;
public anywheresoftware.b4a.objects.ButtonWrapper _buttonce = null;
public anywheresoftware.b4a.objects.ButtonWrapper _buttondiv = null;
public anywheresoftware.b4a.objects.ButtonWrapper _buttonequal = null;
public anywheresoftware.b4a.objects.ButtonWrapper _buttonmemcall = null;
public anywheresoftware.b4a.objects.ButtonWrapper _buttonmemplus = null;
public anywheresoftware.b4a.objects.ButtonWrapper _buttonmemrst = null;
public anywheresoftware.b4a.objects.ButtonWrapper _buttonmul = null;
public anywheresoftware.b4a.objects.ButtonWrapper _buttonsqrt = null;
public anywheresoftware.b4a.objects.ButtonWrapper _buttonsub = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edittext1 = null;
public anywheresoftware.b4a.objects.PanelWrapper _panel1 = null;
public static int _memrststage = 0;
public anywheresoftware.b4a.objects.LabelWrapper _label1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _labelnums = null;
public anywheresoftware.b4a.objects.LabelWrapper _labelmem = null;
public anywheresoftware.b4a.objects.ButtonWrapper _buttonplusminus = null;
public anywheresoftware.b4a.objects.ButtonWrapper _buttonperiod = null;
public anywheresoftware.b4a.objects.drawable.GradientDrawable _gradientgreen = null;
public anywheresoftware.b4a.objects.drawable.GradientDrawable _gradientpinky = null;
public anywheresoftware.b4a.objects.drawable.GradientDrawable _gradientyellow = null;
public anywheresoftware.b4a.objects.drawable.GradientDrawable _gradientred = null;
public anywheresoftware.b4a.objects.drawable.GradientDrawable _gradientwhite = null;
public anywheresoftware.b4a.objects.drawable.GradientDrawable _gradientpurple = null;
public anywheresoftware.b4a.objects.drawable.GradientDrawable _gradientlblue = null;
public anywheresoftware.b4a.objects.drawable.ColorDrawable _solidwhite = null;
public anywheresoftware.b4a.objects.drawable.ColorDrawable _solidpink = null;
public anywheresoftware.b4a.objects.ButtonWrapper _buttontheme = null;
public b4a.example.dateutils _dateutils = null;
public com.halitcan.calc.starter _starter = null;
public com.halitcan.calc.xuiviewsutils _xuiviewsutils = null;
public static String  _activity_create(boolean _firsttime) throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "activity_create", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "activity_create", new Object[] {_firsttime}));}
RDebugUtils.currentLine=196608;
 //BA.debugLineNum = 196608;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
RDebugUtils.currentLine=196612;
 //BA.debugLineNum = 196612;BA.debugLine="Activity.LoadLayout(\"Open\")";
mostCurrent._activity.LoadLayout("Open",mostCurrent.activityBA);
RDebugUtils.currentLine=196613;
 //BA.debugLineNum = 196613;BA.debugLine="lang=CheckLanguage";
mostCurrent._lang = _checklanguage();
RDebugUtils.currentLine=196614;
 //BA.debugLineNum = 196614;BA.debugLine="res.Initialize";
mostCurrent._res.Initialize(processBA);
RDebugUtils.currentLine=196615;
 //BA.debugLineNum = 196615;BA.debugLine="Activity.Title =  res.getString(\"app_name\")";
mostCurrent._activity.setTitle(BA.ObjectToCharSequence(mostCurrent._res.getString(processBA,"app_name")));
RDebugUtils.currentLine=196618;
 //BA.debugLineNum = 196618;BA.debugLine="If FirstTime=True Then";
if (_firsttime==anywheresoftware.b4a.keywords.Common.True) { 
RDebugUtils.currentLine=196619;
 //BA.debugLineNum = 196619;BA.debugLine="EditText1.Text=0";
mostCurrent._edittext1.setText(BA.ObjectToCharSequence(0));
RDebugUtils.currentLine=196620;
 //BA.debugLineNum = 196620;BA.debugLine="LabelNums.Text=\"\"";
mostCurrent._labelnums.setText(BA.ObjectToCharSequence(""));
RDebugUtils.currentLine=196621;
 //BA.debugLineNum = 196621;BA.debugLine="Label1.Text=\"\"";
mostCurrent._label1.setText(BA.ObjectToCharSequence(""));
 };
RDebugUtils.currentLine=196623;
 //BA.debugLineNum = 196623;BA.debugLine="Resize";
_resize();
RDebugUtils.currentLine=196625;
 //BA.debugLineNum = 196625;BA.debugLine="MakeGradient";
_makegradient();
RDebugUtils.currentLine=196629;
 //BA.debugLineNum = 196629;BA.debugLine="End Sub";
return "";
}
public static String  _checklanguage() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "checklanguage", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "checklanguage", null));}
String _language = "";
anywheresoftware.b4a.agraham.reflection.Reflection _r = null;
RDebugUtils.currentLine=524288;
 //BA.debugLineNum = 524288;BA.debugLine="Sub CheckLanguage As String";
RDebugUtils.currentLine=524289;
 //BA.debugLineNum = 524289;BA.debugLine="Dim language As String";
_language = "";
RDebugUtils.currentLine=524290;
 //BA.debugLineNum = 524290;BA.debugLine="Dim r As Reflector";
_r = new anywheresoftware.b4a.agraham.reflection.Reflection();
RDebugUtils.currentLine=524292;
 //BA.debugLineNum = 524292;BA.debugLine="Try";
try {RDebugUtils.currentLine=524293;
 //BA.debugLineNum = 524293;BA.debugLine="r.Target = r.RunStaticMethod(\"java.util.Locale\",";
_r.Target = _r.RunStaticMethod("java.util.Locale","getDefault",(Object[])(anywheresoftware.b4a.keywords.Common.Null),(String[])(anywheresoftware.b4a.keywords.Common.Null));
RDebugUtils.currentLine=524294;
 //BA.debugLineNum = 524294;BA.debugLine="language = r.RunMethod(\"getLanguage\")";
_language = BA.ObjectToString(_r.RunMethod("getLanguage"));
 } 
       catch (Exception e7) {
			processBA.setLastException(e7);RDebugUtils.currentLine=524296;
 //BA.debugLineNum = 524296;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("0524296",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 };
RDebugUtils.currentLine=524299;
 //BA.debugLineNum = 524299;BA.debugLine="Return language.ToLowerCase";
if (true) return _language.toLowerCase();
RDebugUtils.currentLine=524300;
 //BA.debugLineNum = 524300;BA.debugLine="End Sub";
return "";
}
public static String  _resize() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "resize", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "resize", null));}
double _px = 0;
double _py = 0;
int _w = 0;
int _h = 0;
anywheresoftware.b4a.objects.ConcreteViewWrapper _v = null;
int _i = 0;
RDebugUtils.currentLine=1507328;
 //BA.debugLineNum = 1507328;BA.debugLine="Sub Resize";
RDebugUtils.currentLine=1507329;
 //BA.debugLineNum = 1507329;BA.debugLine="Dim PX As Double=1";
_px = 1;
RDebugUtils.currentLine=1507330;
 //BA.debugLineNum = 1507330;BA.debugLine="Dim PY As Double=1";
_py = 1;
RDebugUtils.currentLine=1507331;
 //BA.debugLineNum = 1507331;BA.debugLine="Dim W As Int= Activity.Width";
_w = mostCurrent._activity.getWidth();
RDebugUtils.currentLine=1507332;
 //BA.debugLineNum = 1507332;BA.debugLine="Dim H As Int=Activity.Height";
_h = mostCurrent._activity.getHeight();
RDebugUtils.currentLine=1507333;
 //BA.debugLineNum = 1507333;BA.debugLine="If First =True Then";
if (_first==anywheresoftware.b4a.keywords.Common.True) { 
RDebugUtils.currentLine=1507334;
 //BA.debugLineNum = 1507334;BA.debugLine="First=False";
_first = anywheresoftware.b4a.keywords.Common.False;
RDebugUtils.currentLine=1507335;
 //BA.debugLineNum = 1507335;BA.debugLine="OX = 320' Original Design Size";
_ox = (int) (320);
RDebugUtils.currentLine=1507336;
 //BA.debugLineNum = 1507336;BA.debugLine="OY = 480";
_oy = (int) (480);
RDebugUtils.currentLine=1507337;
 //BA.debugLineNum = 1507337;BA.debugLine="PX = W / (OX*Density) ' Change proportions";
_px = _w/(double)(_ox*anywheresoftware.b4a.keywords.Common.Density);
RDebugUtils.currentLine=1507338;
 //BA.debugLineNum = 1507338;BA.debugLine="PY = H / (OY*Density)";
_py = _h/(double)(_oy*anywheresoftware.b4a.keywords.Common.Density);
 };
RDebugUtils.currentLine=1507341;
 //BA.debugLineNum = 1507341;BA.debugLine="Dim v As View";
_v = new anywheresoftware.b4a.objects.ConcreteViewWrapper();
RDebugUtils.currentLine=1507342;
 //BA.debugLineNum = 1507342;BA.debugLine="Panel1.Visible=False";
mostCurrent._panel1.setVisible(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=1507343;
 //BA.debugLineNum = 1507343;BA.debugLine="For i =0 To Activity.NumberOfViews-1";
{
final int step14 = 1;
final int limit14 = (int) (mostCurrent._activity.getNumberOfViews()-1);
_i = (int) (0) ;
for (;_i <= limit14 ;_i = _i + step14 ) {
RDebugUtils.currentLine=1507344;
 //BA.debugLineNum = 1507344;BA.debugLine="v= Activity.GetView(i)";
_v = mostCurrent._activity.GetView(_i);
RDebugUtils.currentLine=1507346;
 //BA.debugLineNum = 1507346;BA.debugLine="v.Left=v.Left * PX";
_v.setLeft((int) (_v.getLeft()*_px));
RDebugUtils.currentLine=1507347;
 //BA.debugLineNum = 1507347;BA.debugLine="v.Top=v.Top*PY";
_v.setTop((int) (_v.getTop()*_py));
RDebugUtils.currentLine=1507348;
 //BA.debugLineNum = 1507348;BA.debugLine="v.Width=v.Width*PX";
_v.setWidth((int) (_v.getWidth()*_px));
RDebugUtils.currentLine=1507349;
 //BA.debugLineNum = 1507349;BA.debugLine="v.Height=v.Height*PY";
_v.setHeight((int) (_v.getHeight()*_py));
RDebugUtils.currentLine=1507351;
 //BA.debugLineNum = 1507351;BA.debugLine="If v Is Button Then";
if (_v.getObjectOrNull() instanceof android.widget.Button) { 
 };
 }
};
RDebugUtils.currentLine=1507357;
 //BA.debugLineNum = 1507357;BA.debugLine="OX=Activity.Width";
_ox = mostCurrent._activity.getWidth();
RDebugUtils.currentLine=1507358;
 //BA.debugLineNum = 1507358;BA.debugLine="OY=Activity.Height";
_oy = mostCurrent._activity.getHeight();
RDebugUtils.currentLine=1507359;
 //BA.debugLineNum = 1507359;BA.debugLine="Panel1.Visible=True";
mostCurrent._panel1.setVisible(anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=1507361;
 //BA.debugLineNum = 1507361;BA.debugLine="End Sub";
return "";
}
public static String  _makegradient() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "makegradient", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "makegradient", null));}
anywheresoftware.b4a.objects.ConcreteViewWrapper _v = null;
int _cr = 0;
int[] _clrsgrreen = null;
int[] _clrspink = null;
int[] _clrsyellow = null;
int[] _clrsred = null;
int[] _clrswhite = null;
int[] _clrspurple = null;
int[] _clrslblue = null;
anywheresoftware.b4a.objects.drawable.StateListDrawable[] _stddraw = null;
com.halitcan.calc.main._typestate[] _states = null;
int _i = 0;
anywheresoftware.b4a.objects.ButtonWrapper _b = null;
RDebugUtils.currentLine=262144;
 //BA.debugLineNum = 262144;BA.debugLine="Sub MakeGradient";
RDebugUtils.currentLine=262145;
 //BA.debugLineNum = 262145;BA.debugLine="Dim v As View";
_v = new anywheresoftware.b4a.objects.ConcreteViewWrapper();
RDebugUtils.currentLine=262146;
 //BA.debugLineNum = 262146;BA.debugLine="Dim CR As Int=10dip ' CornerRadius";
_cr = anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10));
RDebugUtils.currentLine=262149;
 //BA.debugLineNum = 262149;BA.debugLine="Dim ClrsGRreen(2) As Int";
_clrsgrreen = new int[(int) (2)];
;
RDebugUtils.currentLine=262150;
 //BA.debugLineNum = 262150;BA.debugLine="ClrsGRreen(0) =Colors.ARGB(255,146,219,105)";
_clrsgrreen[(int) (0)] = anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (255),(int) (146),(int) (219),(int) (105));
RDebugUtils.currentLine=262151;
 //BA.debugLineNum = 262151;BA.debugLine="ClrsGRreen(1) = Colors.ARGB(255,55,110,52)";
_clrsgrreen[(int) (1)] = anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (255),(int) (55),(int) (110),(int) (52));
RDebugUtils.currentLine=262152;
 //BA.debugLineNum = 262152;BA.debugLine="GradientGreen.Initialize(\"TOP_BOTTOM\",ClrsGRreen";
mostCurrent._gradientgreen.Initialize(BA.getEnumFromString(android.graphics.drawable.GradientDrawable.Orientation.class,"TOP_BOTTOM"),_clrsgrreen);
RDebugUtils.currentLine=262153;
 //BA.debugLineNum = 262153;BA.debugLine="GradientGreen.CornerRadius=CR";
mostCurrent._gradientgreen.setCornerRadius((float) (_cr));
RDebugUtils.currentLine=262155;
 //BA.debugLineNum = 262155;BA.debugLine="Dim ClrsPink(2) As Int";
_clrspink = new int[(int) (2)];
;
RDebugUtils.currentLine=262156;
 //BA.debugLineNum = 262156;BA.debugLine="ClrsPink(0) =Colors.ARGB(255,255,169,169)";
_clrspink[(int) (0)] = anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (255),(int) (255),(int) (169),(int) (169));
RDebugUtils.currentLine=262157;
 //BA.debugLineNum = 262157;BA.debugLine="ClrsPink(1) = Colors.ARGB(255,175,68,68)";
_clrspink[(int) (1)] = anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (255),(int) (175),(int) (68),(int) (68));
RDebugUtils.currentLine=262158;
 //BA.debugLineNum = 262158;BA.debugLine="GradientPinky.Initialize(\"TOP_BOTTOM\",ClrsPink )";
mostCurrent._gradientpinky.Initialize(BA.getEnumFromString(android.graphics.drawable.GradientDrawable.Orientation.class,"TOP_BOTTOM"),_clrspink);
RDebugUtils.currentLine=262159;
 //BA.debugLineNum = 262159;BA.debugLine="GradientPinky.CornerRadius=CR";
mostCurrent._gradientpinky.setCornerRadius((float) (_cr));
RDebugUtils.currentLine=262162;
 //BA.debugLineNum = 262162;BA.debugLine="Dim ClrsYellow(2) As Int";
_clrsyellow = new int[(int) (2)];
;
RDebugUtils.currentLine=262163;
 //BA.debugLineNum = 262163;BA.debugLine="ClrsYellow(0) =Colors.ARGB(255,255,255,112)";
_clrsyellow[(int) (0)] = anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (255),(int) (255),(int) (255),(int) (112));
RDebugUtils.currentLine=262164;
 //BA.debugLineNum = 262164;BA.debugLine="ClrsYellow(1) = Colors.ARGB(255,129,129,51)";
_clrsyellow[(int) (1)] = anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (255),(int) (129),(int) (129),(int) (51));
RDebugUtils.currentLine=262165;
 //BA.debugLineNum = 262165;BA.debugLine="GradientYellow.Initialize(\"TOP_BOTTOM\", ClrsYello";
mostCurrent._gradientyellow.Initialize(BA.getEnumFromString(android.graphics.drawable.GradientDrawable.Orientation.class,"TOP_BOTTOM"),_clrsyellow);
RDebugUtils.currentLine=262166;
 //BA.debugLineNum = 262166;BA.debugLine="GradientYellow.CornerRadius=CR";
mostCurrent._gradientyellow.setCornerRadius((float) (_cr));
RDebugUtils.currentLine=262169;
 //BA.debugLineNum = 262169;BA.debugLine="Dim ClrsRed(2) As Int";
_clrsred = new int[(int) (2)];
;
RDebugUtils.currentLine=262170;
 //BA.debugLineNum = 262170;BA.debugLine="ClrsRed(0) =Colors.ARGB(255,255,180,180)";
_clrsred[(int) (0)] = anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (255),(int) (255),(int) (180),(int) (180));
RDebugUtils.currentLine=262171;
 //BA.debugLineNum = 262171;BA.debugLine="ClrsRed(1) = Colors.ARGB(255,255,107,107)";
_clrsred[(int) (1)] = anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (255),(int) (255),(int) (107),(int) (107));
RDebugUtils.currentLine=262172;
 //BA.debugLineNum = 262172;BA.debugLine="GradientRed.Initialize(\"TOP_BOTTOM\", ClrsRed)";
mostCurrent._gradientred.Initialize(BA.getEnumFromString(android.graphics.drawable.GradientDrawable.Orientation.class,"TOP_BOTTOM"),_clrsred);
RDebugUtils.currentLine=262173;
 //BA.debugLineNum = 262173;BA.debugLine="GradientRed.CornerRadius=CR";
mostCurrent._gradientred.setCornerRadius((float) (_cr));
RDebugUtils.currentLine=262176;
 //BA.debugLineNum = 262176;BA.debugLine="Dim ClrsWhite(2) As Int";
_clrswhite = new int[(int) (2)];
;
RDebugUtils.currentLine=262177;
 //BA.debugLineNum = 262177;BA.debugLine="ClrsWhite(0) =Colors.White";
_clrswhite[(int) (0)] = anywheresoftware.b4a.keywords.Common.Colors.White;
RDebugUtils.currentLine=262178;
 //BA.debugLineNum = 262178;BA.debugLine="ClrsWhite(1) = Colors.ARGB(255,200,200,200)";
_clrswhite[(int) (1)] = anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (255),(int) (200),(int) (200),(int) (200));
RDebugUtils.currentLine=262179;
 //BA.debugLineNum = 262179;BA.debugLine="GradientWhite.Initialize(\"TOP_BOTTOM\", ClrsWhite)";
mostCurrent._gradientwhite.Initialize(BA.getEnumFromString(android.graphics.drawable.GradientDrawable.Orientation.class,"TOP_BOTTOM"),_clrswhite);
RDebugUtils.currentLine=262180;
 //BA.debugLineNum = 262180;BA.debugLine="GradientWhite.CornerRadius=CR";
mostCurrent._gradientwhite.setCornerRadius((float) (_cr));
RDebugUtils.currentLine=262183;
 //BA.debugLineNum = 262183;BA.debugLine="Dim ClrsPurple(2) As Int";
_clrspurple = new int[(int) (2)];
;
RDebugUtils.currentLine=262184;
 //BA.debugLineNum = 262184;BA.debugLine="ClrsPurple(0) =Colors.ARGB(255,255,0,255)";
_clrspurple[(int) (0)] = anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (255),(int) (255),(int) (0),(int) (255));
RDebugUtils.currentLine=262185;
 //BA.debugLineNum = 262185;BA.debugLine="ClrsPurple(1) = Colors.ARGB(255,180,0,180)";
_clrspurple[(int) (1)] = anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (255),(int) (180),(int) (0),(int) (180));
RDebugUtils.currentLine=262186;
 //BA.debugLineNum = 262186;BA.debugLine="GradientPurple.Initialize(\"TOP_BOTTOM\", ClrsPurpl";
mostCurrent._gradientpurple.Initialize(BA.getEnumFromString(android.graphics.drawable.GradientDrawable.Orientation.class,"TOP_BOTTOM"),_clrspurple);
RDebugUtils.currentLine=262187;
 //BA.debugLineNum = 262187;BA.debugLine="GradientPurple.CornerRadius=CR";
mostCurrent._gradientpurple.setCornerRadius((float) (_cr));
RDebugUtils.currentLine=262190;
 //BA.debugLineNum = 262190;BA.debugLine="Dim ClrsLBlue(2) As Int";
_clrslblue = new int[(int) (2)];
;
RDebugUtils.currentLine=262191;
 //BA.debugLineNum = 262191;BA.debugLine="ClrsLBlue(0) =Colors.ARGB(255,243,255,255)";
_clrslblue[(int) (0)] = anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (255),(int) (243),(int) (255),(int) (255));
RDebugUtils.currentLine=262192;
 //BA.debugLineNum = 262192;BA.debugLine="ClrsLBlue(1) = Colors.ARGB(255,165,215,231)";
_clrslblue[(int) (1)] = anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (255),(int) (165),(int) (215),(int) (231));
RDebugUtils.currentLine=262193;
 //BA.debugLineNum = 262193;BA.debugLine="GradientLBlue.Initialize(\"TOP_BOTTOM\", ClrsLBlue)";
mostCurrent._gradientlblue.Initialize(BA.getEnumFromString(android.graphics.drawable.GradientDrawable.Orientation.class,"TOP_BOTTOM"),_clrslblue);
RDebugUtils.currentLine=262194;
 //BA.debugLineNum = 262194;BA.debugLine="GradientLBlue.CornerRadius=CR";
mostCurrent._gradientlblue.setCornerRadius((float) (_cr));
RDebugUtils.currentLine=262197;
 //BA.debugLineNum = 262197;BA.debugLine="Dim stdDraw(Activity.NumberOfViews) As StateListD";
_stddraw = new anywheresoftware.b4a.objects.drawable.StateListDrawable[mostCurrent._activity.getNumberOfViews()];
{
int d0 = _stddraw.length;
for (int i0 = 0;i0 < d0;i0++) {
_stddraw[i0] = new anywheresoftware.b4a.objects.drawable.StateListDrawable();
}
}
;
RDebugUtils.currentLine=262198;
 //BA.debugLineNum = 262198;BA.debugLine="Dim states((Activity.NumberOfViews)) As TypeState";
_states = new com.halitcan.calc.main._typestate[(mostCurrent._activity.getNumberOfViews())];
{
int d0 = _states.length;
for (int i0 = 0;i0 < d0;i0++) {
_states[i0] = new com.halitcan.calc.main._typestate();
}
}
;
RDebugUtils.currentLine=262200;
 //BA.debugLineNum = 262200;BA.debugLine="For i =0 To Activity.NumberOfViews-1";
{
final int step40 = 1;
final int limit40 = (int) (mostCurrent._activity.getNumberOfViews()-1);
_i = (int) (0) ;
for (;_i <= limit40 ;_i = _i + step40 ) {
RDebugUtils.currentLine=262201;
 //BA.debugLineNum = 262201;BA.debugLine="v= Activity.GetView(i)";
_v = mostCurrent._activity.GetView(_i);
RDebugUtils.currentLine=262202;
 //BA.debugLineNum = 262202;BA.debugLine="states(i).Initialize";
_states[_i].Initialize();
RDebugUtils.currentLine=262203;
 //BA.debugLineNum = 262203;BA.debugLine="stdDraw(i).Initialize";
_stddraw[_i].Initialize();
RDebugUtils.currentLine=262205;
 //BA.debugLineNum = 262205;BA.debugLine="If v Is Button Then";
if (_v.getObjectOrNull() instanceof android.widget.Button) { 
RDebugUtils.currentLine=262206;
 //BA.debugLineNum = 262206;BA.debugLine="Dim b As Button = v";
_b = new anywheresoftware.b4a.objects.ButtonWrapper();
_b = (anywheresoftware.b4a.objects.ButtonWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ButtonWrapper(), (android.widget.Button)(_v.getObject()));
RDebugUtils.currentLine=262207;
 //BA.debugLineNum = 262207;BA.debugLine="Select v.Tag	'View tags used for styles";
switch (BA.switchObjectToInt(_v.getTag(),(Object)("digit"),(Object)("equal"),(Object)("math"),(Object)("red"),(Object)("yellow"))) {
case 0: {
RDebugUtils.currentLine=262210;
 //BA.debugLineNum = 262210;BA.debugLine="states(i).state2(0)=stdDraw(i).State_Enabled";
_states[_i].state2 /*int[]*/ [(int) (0)] = _stddraw[_i].State_Enabled;
RDebugUtils.currentLine=262211;
 //BA.debugLineNum = 262211;BA.debugLine="states(i).state2(1)= -stdDraw(i).State_Presse";
_states[_i].state2 /*int[]*/ [(int) (1)] = (int) (-_stddraw[_i].State_Pressed);
RDebugUtils.currentLine=262212;
 //BA.debugLineNum = 262212;BA.debugLine="stdDraw(i).AddState2(states(i).state2,Gradien";
_stddraw[_i].AddState2(_states[_i].state2 /*int[]*/ ,(android.graphics.drawable.Drawable)(mostCurrent._gradientwhite.getObject()));
RDebugUtils.currentLine=262213;
 //BA.debugLineNum = 262213;BA.debugLine="states(i).state1=stdDraw(i).State_Pressed";
_states[_i].state1 /*int*/  = _stddraw[_i].State_Pressed;
RDebugUtils.currentLine=262214;
 //BA.debugLineNum = 262214;BA.debugLine="stdDraw(i).AddState(states(i).state1,Gradient";
_stddraw[_i].AddState(_states[_i].state1 /*int*/ ,(android.graphics.drawable.Drawable)(mostCurrent._gradientred.getObject()));
RDebugUtils.currentLine=262216;
 //BA.debugLineNum = 262216;BA.debugLine="b.Background=stdDraw(i)";
_b.setBackground((android.graphics.drawable.Drawable)(_stddraw[_i].getObject()));
 break; }
case 1: {
RDebugUtils.currentLine=262218;
 //BA.debugLineNum = 262218;BA.debugLine="states(i).state2(0)=stdDraw(i).State_Enabled";
_states[_i].state2 /*int[]*/ [(int) (0)] = _stddraw[_i].State_Enabled;
RDebugUtils.currentLine=262219;
 //BA.debugLineNum = 262219;BA.debugLine="states(i).state2(1)= -stdDraw(i).State_Presse";
_states[_i].state2 /*int[]*/ [(int) (1)] = (int) (-_stddraw[_i].State_Pressed);
RDebugUtils.currentLine=262220;
 //BA.debugLineNum = 262220;BA.debugLine="stdDraw(i).AddState2(states(i).state2,Gradien";
_stddraw[_i].AddState2(_states[_i].state2 /*int[]*/ ,(android.graphics.drawable.Drawable)(mostCurrent._gradientlblue.getObject()));
RDebugUtils.currentLine=262221;
 //BA.debugLineNum = 262221;BA.debugLine="states(i).state1=stdDraw(i).State_Pressed";
_states[_i].state1 /*int*/  = _stddraw[_i].State_Pressed;
RDebugUtils.currentLine=262222;
 //BA.debugLineNum = 262222;BA.debugLine="stdDraw(i).AddState(states(i).state1,Gradient";
_stddraw[_i].AddState(_states[_i].state1 /*int*/ ,(android.graphics.drawable.Drawable)(mostCurrent._gradientred.getObject()));
RDebugUtils.currentLine=262224;
 //BA.debugLineNum = 262224;BA.debugLine="b.Background=stdDraw(i)";
_b.setBackground((android.graphics.drawable.Drawable)(_stddraw[_i].getObject()));
 break; }
case 2: {
RDebugUtils.currentLine=262226;
 //BA.debugLineNum = 262226;BA.debugLine="b.Background=GradientPurple";
_b.setBackground((android.graphics.drawable.Drawable)(mostCurrent._gradientpurple.getObject()));
 break; }
case 3: {
RDebugUtils.currentLine=262229;
 //BA.debugLineNum = 262229;BA.debugLine="b.Background=GradientRed";
_b.setBackground((android.graphics.drawable.Drawable)(mostCurrent._gradientred.getObject()));
 break; }
case 4: {
RDebugUtils.currentLine=262232;
 //BA.debugLineNum = 262232;BA.debugLine="b.Background=GradientYellow";
_b.setBackground((android.graphics.drawable.Drawable)(mostCurrent._gradientyellow.getObject()));
 break; }
}
;
RDebugUtils.currentLine=262237;
 //BA.debugLineNum = 262237;BA.debugLine="b.Invalidate";
_b.Invalidate();
 };
 }
};
RDebugUtils.currentLine=262240;
 //BA.debugLineNum = 262240;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
RDebugUtils.currentModule="main";
RDebugUtils.currentLine=458752;
 //BA.debugLineNum = 458752;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
RDebugUtils.currentLine=458754;
 //BA.debugLineNum = 458754;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "activity_resume", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "activity_resume", null));}
RDebugUtils.currentLine=393216;
 //BA.debugLineNum = 393216;BA.debugLine="Sub Activity_Resume";
RDebugUtils.currentLine=393217;
 //BA.debugLineNum = 393217;BA.debugLine="Resize";
_resize();
RDebugUtils.currentLine=393218;
 //BA.debugLineNum = 393218;BA.debugLine="lang=CheckLanguage";
mostCurrent._lang = _checklanguage();
RDebugUtils.currentLine=393219;
 //BA.debugLineNum = 393219;BA.debugLine="Log(lang)";
anywheresoftware.b4a.keywords.Common.LogImpl("0393219",mostCurrent._lang,0);
RDebugUtils.currentLine=393220;
 //BA.debugLineNum = 393220;BA.debugLine="SetLanguage";
_setlanguage();
RDebugUtils.currentLine=393221;
 //BA.debugLineNum = 393221;BA.debugLine="MakeGradient";
_makegradient();
RDebugUtils.currentLine=393222;
 //BA.debugLineNum = 393222;BA.debugLine="End Sub";
return "";
}
public static String  _setlanguage() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "setlanguage", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "setlanguage", null));}
RDebugUtils.currentLine=131072;
 //BA.debugLineNum = 131072;BA.debugLine="Sub SetLanguage";
RDebugUtils.currentLine=131073;
 //BA.debugLineNum = 131073;BA.debugLine="If lang=\"tr\" Then";
if ((mostCurrent._lang).equals("tr")) { 
RDebugUtils.currentLine=131074;
 //BA.debugLineNum = 131074;BA.debugLine="strClearMemory=\"Hafızayı silmek için 2 defa bası";
mostCurrent._strclearmemory = "Hafızayı silmek için 2 defa basın: ";
RDebugUtils.currentLine=131075;
 //BA.debugLineNum = 131075;BA.debugLine="strCallMemory=\"Hafızadan alınan :\"";
mostCurrent._strcallmemory = "Hafızadan alınan :";
RDebugUtils.currentLine=131076;
 //BA.debugLineNum = 131076;BA.debugLine="strSavedToMem=\"Hafızaya alınan: \"";
mostCurrent._strsavedtomem = "Hafızaya alınan: ";
RDebugUtils.currentLine=131077;
 //BA.debugLineNum = 131077;BA.debugLine="strMemClerared=\"Hafıza Silindi\"";
mostCurrent._strmemclerared = "Hafıza Silindi";
 }else {
RDebugUtils.currentLine=131080;
 //BA.debugLineNum = 131080;BA.debugLine="strClearMemory=\"Press again to clear memory: \"";
mostCurrent._strclearmemory = "Press again to clear memory: ";
RDebugUtils.currentLine=131081;
 //BA.debugLineNum = 131081;BA.debugLine="strCallMemory=\"Called from memory :\"";
mostCurrent._strcallmemory = "Called from memory :";
RDebugUtils.currentLine=131082;
 //BA.debugLineNum = 131082;BA.debugLine="strSavedToMem=\"Saved to memory: \"";
mostCurrent._strsavedtomem = "Saved to memory: ";
RDebugUtils.currentLine=131083;
 //BA.debugLineNum = 131083;BA.debugLine="strMemClerared=\"Memory cleared\"";
mostCurrent._strmemclerared = "Memory cleared";
 };
RDebugUtils.currentLine=131085;
 //BA.debugLineNum = 131085;BA.debugLine="End Sub";
return "";
}
public static String  _buttonadd_click() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "buttonadd_click", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "buttonadd_click", null));}
RDebugUtils.currentLine=851968;
 //BA.debugLineNum = 851968;BA.debugLine="Sub ButtonAdd_Click";
RDebugUtils.currentLine=851969;
 //BA.debugLineNum = 851969;BA.debugLine="OPERATION=OPERATION_SUM";
_operation = _operation_sum;
RDebugUtils.currentLine=851970;
 //BA.debugLineNum = 851970;BA.debugLine="STAGE=STAGE2";
_stage = _stage2;
RDebugUtils.currentLine=851971;
 //BA.debugLineNum = 851971;BA.debugLine="Label1.Text=\"+\"";
mostCurrent._label1.setText(BA.ObjectToCharSequence("+"));
RDebugUtils.currentLine=851972;
 //BA.debugLineNum = 851972;BA.debugLine="ToastMessageShow(\"+\",False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("+"),anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=851973;
 //BA.debugLineNum = 851973;BA.debugLine="End Sub";
return "";
}
public static String  _buttonback_click() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "buttonback_click", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "buttonback_click", null));}
RDebugUtils.currentLine=1441792;
 //BA.debugLineNum = 1441792;BA.debugLine="Sub ButtonBack_Click";
RDebugUtils.currentLine=1441793;
 //BA.debugLineNum = 1441793;BA.debugLine="Try";
try {RDebugUtils.currentLine=1441795;
 //BA.debugLineNum = 1441795;BA.debugLine="If EditText1.Text.Length=1 Then";
if (mostCurrent._edittext1.getText().length()==1) { 
RDebugUtils.currentLine=1441796;
 //BA.debugLineNum = 1441796;BA.debugLine="EditText1.Text=\"0\"";
mostCurrent._edittext1.setText(BA.ObjectToCharSequence("0"));
RDebugUtils.currentLine=1441797;
 //BA.debugLineNum = 1441797;BA.debugLine="Log(\"0\")";
anywheresoftware.b4a.keywords.Common.LogImpl("01441797","0",0);
 }else {
RDebugUtils.currentLine=1441799;
 //BA.debugLineNum = 1441799;BA.debugLine="EditText1.Text = EditText1.Text.Substring2(0,Edi";
mostCurrent._edittext1.setText(BA.ObjectToCharSequence(mostCurrent._edittext1.getText().substring((int) (0),(int) (mostCurrent._edittext1.getText().length()-1))));
 };
 } 
       catch (Exception e9) {
			processBA.setLastException(e9);RDebugUtils.currentLine=1441802;
 //BA.debugLineNum = 1441802;BA.debugLine="EditText1.Text=\"0\"";
mostCurrent._edittext1.setText(BA.ObjectToCharSequence("0"));
RDebugUtils.currentLine=1441803;
 //BA.debugLineNum = 1441803;BA.debugLine="Log(\"Catch\")";
anywheresoftware.b4a.keywords.Common.LogImpl("01441803","Catch",0);
 };
RDebugUtils.currentLine=1441807;
 //BA.debugLineNum = 1441807;BA.debugLine="End Sub";
return "";
}
public static String  _buttonce_click() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "buttonce_click", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "buttonce_click", null));}
RDebugUtils.currentLine=1376256;
 //BA.debugLineNum = 1376256;BA.debugLine="Sub ButtonCE_Click";
RDebugUtils.currentLine=1376257;
 //BA.debugLineNum = 1376257;BA.debugLine="EditText1.Text=0";
mostCurrent._edittext1.setText(BA.ObjectToCharSequence(0));
RDebugUtils.currentLine=1376258;
 //BA.debugLineNum = 1376258;BA.debugLine="Label1.Text=\"\"";
mostCurrent._label1.setText(BA.ObjectToCharSequence(""));
RDebugUtils.currentLine=1376259;
 //BA.debugLineNum = 1376259;BA.debugLine="num1=0";
_num1 = 0;
RDebugUtils.currentLine=1376260;
 //BA.debugLineNum = 1376260;BA.debugLine="num2=0";
_num2 = 0;
RDebugUtils.currentLine=1376261;
 //BA.debugLineNum = 1376261;BA.debugLine="numResult=0";
_numresult = 0;
RDebugUtils.currentLine=1376262;
 //BA.debugLineNum = 1376262;BA.debugLine="STAGE=STAGE1";
_stage = _stage1;
RDebugUtils.currentLine=1376263;
 //BA.debugLineNum = 1376263;BA.debugLine="End Sub";
return "";
}
public static String  _buttondigit_click() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "buttondigit_click", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "buttondigit_click", null));}
anywheresoftware.b4a.objects.ButtonWrapper _btn = null;
RDebugUtils.currentLine=655360;
 //BA.debugLineNum = 655360;BA.debugLine="Sub ButtonDigit_Click";
RDebugUtils.currentLine=655361;
 //BA.debugLineNum = 655361;BA.debugLine="Dim btn As Button = Sender";
_btn = new anywheresoftware.b4a.objects.ButtonWrapper();
_btn = (anywheresoftware.b4a.objects.ButtonWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ButtonWrapper(), (android.widget.Button)(anywheresoftware.b4a.keywords.Common.Sender(mostCurrent.activityBA)));
RDebugUtils.currentLine=655363;
 //BA.debugLineNum = 655363;BA.debugLine="If STAGE=STAGE_RESULT Then";
if (_stage==_stage_result) { 
RDebugUtils.currentLine=655364;
 //BA.debugLineNum = 655364;BA.debugLine="STAGE=STAGE1";
_stage = _stage1;
RDebugUtils.currentLine=655365;
 //BA.debugLineNum = 655365;BA.debugLine="EditText1.Text=\"0\"";
mostCurrent._edittext1.setText(BA.ObjectToCharSequence("0"));
RDebugUtils.currentLine=655366;
 //BA.debugLineNum = 655366;BA.debugLine="num1=0";
_num1 = 0;
RDebugUtils.currentLine=655367;
 //BA.debugLineNum = 655367;BA.debugLine="num2=0";
_num2 = 0;
RDebugUtils.currentLine=655368;
 //BA.debugLineNum = 655368;BA.debugLine="numResult=0";
_numresult = 0;
 };
RDebugUtils.currentLine=655372;
 //BA.debugLineNum = 655372;BA.debugLine="If EditText1.Text=\"0\" Then EditText1.Text =\"\"";
if ((mostCurrent._edittext1.getText()).equals("0")) { 
mostCurrent._edittext1.setText(BA.ObjectToCharSequence(""));};
RDebugUtils.currentLine=655374;
 //BA.debugLineNum = 655374;BA.debugLine="EditText1.Text=EditText1.Text & btn.Text";
mostCurrent._edittext1.setText(BA.ObjectToCharSequence(mostCurrent._edittext1.getText()+_btn.getText()));
RDebugUtils.currentLine=655376;
 //BA.debugLineNum = 655376;BA.debugLine="If STAGE=STAGE1 Then";
if (_stage==_stage1) { 
RDebugUtils.currentLine=655377;
 //BA.debugLineNum = 655377;BA.debugLine="num1= EditText1.Text";
_num1 = (double)(Double.parseDouble(mostCurrent._edittext1.getText()));
 }else 
{RDebugUtils.currentLine=655378;
 //BA.debugLineNum = 655378;BA.debugLine="Else If STAGE=STAGE2 Then";
if (_stage==_stage2) { 
RDebugUtils.currentLine=655379;
 //BA.debugLineNum = 655379;BA.debugLine="num2= btn.Text";
_num2 = (double)(Double.parseDouble(_btn.getText()));
RDebugUtils.currentLine=655380;
 //BA.debugLineNum = 655380;BA.debugLine="EditText1.Text=btn.Text";
mostCurrent._edittext1.setText(BA.ObjectToCharSequence(_btn.getText()));
RDebugUtils.currentLine=655381;
 //BA.debugLineNum = 655381;BA.debugLine="STAGE=STAGE3";
_stage = _stage3;
 }else 
{RDebugUtils.currentLine=655382;
 //BA.debugLineNum = 655382;BA.debugLine="else if STAGE=STAGE3 Then";
if (_stage==_stage3) { 
RDebugUtils.currentLine=655383;
 //BA.debugLineNum = 655383;BA.debugLine="num2= EditText1.Text";
_num2 = (double)(Double.parseDouble(mostCurrent._edittext1.getText()));
 }}}
;
RDebugUtils.currentLine=655388;
 //BA.debugLineNum = 655388;BA.debugLine="End Sub";
return "";
}
public static String  _buttondiv_click() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "buttondiv_click", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "buttondiv_click", null));}
RDebugUtils.currentLine=1048576;
 //BA.debugLineNum = 1048576;BA.debugLine="Sub ButtonDiv_Click";
RDebugUtils.currentLine=1048577;
 //BA.debugLineNum = 1048577;BA.debugLine="OPERATION=OPERATION_DIVISION";
_operation = _operation_division;
RDebugUtils.currentLine=1048578;
 //BA.debugLineNum = 1048578;BA.debugLine="STAGE=STAGE2";
_stage = _stage2;
RDebugUtils.currentLine=1048579;
 //BA.debugLineNum = 1048579;BA.debugLine="Label1.Text=\"/\"";
mostCurrent._label1.setText(BA.ObjectToCharSequence("/"));
RDebugUtils.currentLine=1048580;
 //BA.debugLineNum = 1048580;BA.debugLine="ToastMessageShow(\"/\",False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("/"),anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=1048581;
 //BA.debugLineNum = 1048581;BA.debugLine="End Sub";
return "";
}
public static String  _buttonequal_click() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "buttonequal_click", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "buttonequal_click", null));}
int _intresult = 0;
RDebugUtils.currentLine=720896;
 //BA.debugLineNum = 720896;BA.debugLine="Sub ButtonEqual_Click";
RDebugUtils.currentLine=720897;
 //BA.debugLineNum = 720897;BA.debugLine="If STAGE=STAGE3 Then";
if (_stage==_stage3) { 
RDebugUtils.currentLine=720898;
 //BA.debugLineNum = 720898;BA.debugLine="Select Case OPERATION";
switch (BA.switchObjectToInt(_operation,_operation_sum,_operation_subtract,_operation_multiply,_operation_division)) {
case 0: {
RDebugUtils.currentLine=720900;
 //BA.debugLineNum = 720900;BA.debugLine="numResult=num1+num2";
_numresult = _num1+_num2;
 break; }
case 1: {
RDebugUtils.currentLine=720903;
 //BA.debugLineNum = 720903;BA.debugLine="numResult=num1-num2";
_numresult = _num1-_num2;
 break; }
case 2: {
RDebugUtils.currentLine=720906;
 //BA.debugLineNum = 720906;BA.debugLine="numResult=num1*num2";
_numresult = _num1*_num2;
 break; }
case 3: {
RDebugUtils.currentLine=720909;
 //BA.debugLineNum = 720909;BA.debugLine="numResult=num1/num2";
_numresult = _num1/(double)_num2;
 break; }
}
;
RDebugUtils.currentLine=720912;
 //BA.debugLineNum = 720912;BA.debugLine="Dim intResult As Int= Round(numResult)";
_intresult = (int) (anywheresoftware.b4a.keywords.Common.Round(_numresult));
RDebugUtils.currentLine=720913;
 //BA.debugLineNum = 720913;BA.debugLine="If intResult = numResult Then ' Tam sayı olma ko";
if (_intresult==_numresult) { 
RDebugUtils.currentLine=720914;
 //BA.debugLineNum = 720914;BA.debugLine="EditText1.Text=intResult";
mostCurrent._edittext1.setText(BA.ObjectToCharSequence(_intresult));
 }else {
RDebugUtils.currentLine=720916;
 //BA.debugLineNum = 720916;BA.debugLine="EditText1.Text=numResult";
mostCurrent._edittext1.setText(BA.ObjectToCharSequence(_numresult));
 };
RDebugUtils.currentLine=720918;
 //BA.debugLineNum = 720918;BA.debugLine="Log(\"num1: \" & num1 &\" Num2: \" &num2 &\" Result: \"";
anywheresoftware.b4a.keywords.Common.LogImpl("0720918","num1: "+BA.NumberToString(_num1)+" Num2: "+BA.NumberToString(_num2)+" Result: "+BA.NumberToString(_numresult)+"IntResult: "+BA.NumberToString(_intresult),0);
RDebugUtils.currentLine=720920;
 //BA.debugLineNum = 720920;BA.debugLine="LabelNums.Text =num1 & \" \"  _ 		& Label1.Text &";
mostCurrent._labelnums.setText(BA.ObjectToCharSequence(BA.NumberToString(_num1)+" "+mostCurrent._label1.getText()+" "+BA.NumberToString(_num2)+" = "+mostCurrent._edittext1.getText()));
RDebugUtils.currentLine=720925;
 //BA.debugLineNum = 720925;BA.debugLine="Label1.Text=\"\"";
mostCurrent._label1.setText(BA.ObjectToCharSequence(""));
RDebugUtils.currentLine=720926;
 //BA.debugLineNum = 720926;BA.debugLine="STAGE=STAGE_RESULT";
_stage = _stage_result;
 };
RDebugUtils.currentLine=720929;
 //BA.debugLineNum = 720929;BA.debugLine="End Sub";
return "";
}
public static String  _buttonmemcall_click() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "buttonmemcall_click", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "buttonmemcall_click", null));}
String _memresult = "";
RDebugUtils.currentLine=1179648;
 //BA.debugLineNum = 1179648;BA.debugLine="Sub ButtonMemCall_Click";
RDebugUtils.currentLine=1179649;
 //BA.debugLineNum = 1179649;BA.debugLine="Dim memResult As String= IntorFloat(mem)";
_memresult = _intorfloat(BA.NumberToString(_mem));
RDebugUtils.currentLine=1179650;
 //BA.debugLineNum = 1179650;BA.debugLine="EditText1.Text= IntorFloat(memResult)";
mostCurrent._edittext1.setText(BA.ObjectToCharSequence(_intorfloat(_memresult)));
RDebugUtils.currentLine=1179651;
 //BA.debugLineNum = 1179651;BA.debugLine="ToastMessageShow(strCallMemory & memResult,False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence(mostCurrent._strcallmemory+_memresult),anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=1179652;
 //BA.debugLineNum = 1179652;BA.debugLine="End Sub";
return "";
}
public static String  _intorfloat(String _floatnum) throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "intorfloat", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "intorfloat", new Object[] {_floatnum}));}
int _intresult = 0;
RDebugUtils.currentLine=786432;
 //BA.debugLineNum = 786432;BA.debugLine="Sub IntorFloat(floatNum  As String) As String";
RDebugUtils.currentLine=786433;
 //BA.debugLineNum = 786433;BA.debugLine="Dim intResult As Int= Round(floatNum)";
_intresult = (int) (anywheresoftware.b4a.keywords.Common.Round((double)(Double.parseDouble(_floatnum))));
RDebugUtils.currentLine=786434;
 //BA.debugLineNum = 786434;BA.debugLine="If intResult = numResult Then ' Tam sayı olma kon";
if (_intresult==_numresult) { 
RDebugUtils.currentLine=786435;
 //BA.debugLineNum = 786435;BA.debugLine="Return intResult";
if (true) return BA.NumberToString(_intresult);
 }else {
RDebugUtils.currentLine=786437;
 //BA.debugLineNum = 786437;BA.debugLine="Return floatNum";
if (true) return _floatnum;
 };
RDebugUtils.currentLine=786440;
 //BA.debugLineNum = 786440;BA.debugLine="End Sub";
return "";
}
public static String  _buttonmemplus_click() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "buttonmemplus_click", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "buttonmemplus_click", null));}
RDebugUtils.currentLine=1114112;
 //BA.debugLineNum = 1114112;BA.debugLine="Sub ButtonMemPlus_Click";
RDebugUtils.currentLine=1114113;
 //BA.debugLineNum = 1114113;BA.debugLine="mem=EditText1.Text";
_mem = (double)(Double.parseDouble(mostCurrent._edittext1.getText()));
RDebugUtils.currentLine=1114114;
 //BA.debugLineNum = 1114114;BA.debugLine="ToastMessageShow(strSavedToMem & mem,False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence(mostCurrent._strsavedtomem+BA.NumberToString(_mem)),anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=1114115;
 //BA.debugLineNum = 1114115;BA.debugLine="LabelMem.Text=mem";
mostCurrent._labelmem.setText(BA.ObjectToCharSequence(_mem));
RDebugUtils.currentLine=1114116;
 //BA.debugLineNum = 1114116;BA.debugLine="End Sub";
return "";
}
public static String  _buttonmemrst_click() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "buttonmemrst_click", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "buttonmemrst_click", null));}
String _memresult = "";
RDebugUtils.currentLine=1245184;
 //BA.debugLineNum = 1245184;BA.debugLine="Sub ButtonMemRst_Click";
RDebugUtils.currentLine=1245185;
 //BA.debugLineNum = 1245185;BA.debugLine="Dim memResult As String= IntorFloat(mem)";
_memresult = _intorfloat(BA.NumberToString(_mem));
RDebugUtils.currentLine=1245186;
 //BA.debugLineNum = 1245186;BA.debugLine="If memrstStage=0 Then";
if (_memrststage==0) { 
RDebugUtils.currentLine=1245187;
 //BA.debugLineNum = 1245187;BA.debugLine="memrstStage=1";
_memrststage = (int) (1);
RDebugUtils.currentLine=1245188;
 //BA.debugLineNum = 1245188;BA.debugLine="ToastMessageShow(strClearMemory & memResult,Fals";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence(mostCurrent._strclearmemory+_memresult),anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=1245189;
 //BA.debugLineNum = 1245189;BA.debugLine="Return";
if (true) return "";
 }else 
{RDebugUtils.currentLine=1245190;
 //BA.debugLineNum = 1245190;BA.debugLine="Else If memrstStage=1 Then";
if (_memrststage==1) { 
RDebugUtils.currentLine=1245191;
 //BA.debugLineNum = 1245191;BA.debugLine="mem=0";
_mem = 0;
RDebugUtils.currentLine=1245192;
 //BA.debugLineNum = 1245192;BA.debugLine="memrstStage=0";
_memrststage = (int) (0);
RDebugUtils.currentLine=1245193;
 //BA.debugLineNum = 1245193;BA.debugLine="LabelMem.Text=\"\"";
mostCurrent._labelmem.setText(BA.ObjectToCharSequence(""));
RDebugUtils.currentLine=1245194;
 //BA.debugLineNum = 1245194;BA.debugLine="ToastMessageShow(strMemClerared,False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence(mostCurrent._strmemclerared),anywheresoftware.b4a.keywords.Common.False);
 }}
;
RDebugUtils.currentLine=1245196;
 //BA.debugLineNum = 1245196;BA.debugLine="End Sub";
return "";
}
public static String  _buttonmul_click() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "buttonmul_click", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "buttonmul_click", null));}
RDebugUtils.currentLine=983040;
 //BA.debugLineNum = 983040;BA.debugLine="Sub ButtonMul_Click";
RDebugUtils.currentLine=983041;
 //BA.debugLineNum = 983041;BA.debugLine="OPERATION=OPERATION_MULTIPLY";
_operation = _operation_multiply;
RDebugUtils.currentLine=983042;
 //BA.debugLineNum = 983042;BA.debugLine="STAGE=STAGE2";
_stage = _stage2;
RDebugUtils.currentLine=983043;
 //BA.debugLineNum = 983043;BA.debugLine="Label1.Text=\"x\"";
mostCurrent._label1.setText(BA.ObjectToCharSequence("x"));
RDebugUtils.currentLine=983044;
 //BA.debugLineNum = 983044;BA.debugLine="ToastMessageShow(\"*\",False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("*"),anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=983045;
 //BA.debugLineNum = 983045;BA.debugLine="End Sub";
return "";
}
public static String  _buttonperiod_click() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "buttonperiod_click", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "buttonperiod_click", null));}
anywheresoftware.b4a.objects.ButtonWrapper _btn = null;
RDebugUtils.currentLine=589824;
 //BA.debugLineNum = 589824;BA.debugLine="Sub ButtonPeriod_Click";
RDebugUtils.currentLine=589826;
 //BA.debugLineNum = 589826;BA.debugLine="Dim btn As Button = Sender";
_btn = new anywheresoftware.b4a.objects.ButtonWrapper();
_btn = (anywheresoftware.b4a.objects.ButtonWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ButtonWrapper(), (android.widget.Button)(anywheresoftware.b4a.keywords.Common.Sender(mostCurrent.activityBA)));
RDebugUtils.currentLine=589827;
 //BA.debugLineNum = 589827;BA.debugLine="If STAGE=STAGE2 And EditText1.Text<>\"0\" Then";
if (_stage==_stage2 && (mostCurrent._edittext1.getText()).equals("0") == false) { 
RDebugUtils.currentLine=589828;
 //BA.debugLineNum = 589828;BA.debugLine="EditText1.Text=\"0.\"";
mostCurrent._edittext1.setText(BA.ObjectToCharSequence("0."));
RDebugUtils.currentLine=589829;
 //BA.debugLineNum = 589829;BA.debugLine="STAGE=STAGE3";
_stage = _stage3;
 }else 
{RDebugUtils.currentLine=589830;
 //BA.debugLineNum = 589830;BA.debugLine="else If EditText1.Text.IndexOf(\".\")==-1 Then";
if (mostCurrent._edittext1.getText().indexOf(".")==-1) { 
RDebugUtils.currentLine=589831;
 //BA.debugLineNum = 589831;BA.debugLine="EditText1.Text=EditText1.Text & btn.Text";
mostCurrent._edittext1.setText(BA.ObjectToCharSequence(mostCurrent._edittext1.getText()+_btn.getText()));
 }}
;
RDebugUtils.currentLine=589836;
 //BA.debugLineNum = 589836;BA.debugLine="End Sub";
return "";
}
public static String  _buttonplusminus_click() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "buttonplusminus_click", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "buttonplusminus_click", null));}
double _f = 0;
RDebugUtils.currentLine=1703936;
 //BA.debugLineNum = 1703936;BA.debugLine="Sub ButtonPlusMinus_Click";
RDebugUtils.currentLine=1703937;
 //BA.debugLineNum = 1703937;BA.debugLine="Dim f As Double= EditText1.Text";
_f = (double)(Double.parseDouble(mostCurrent._edittext1.getText()));
RDebugUtils.currentLine=1703938;
 //BA.debugLineNum = 1703938;BA.debugLine="f=f*-1";
_f = _f*-1;
RDebugUtils.currentLine=1703939;
 //BA.debugLineNum = 1703939;BA.debugLine="EditText1.Text=IntorFloat(f)";
mostCurrent._edittext1.setText(BA.ObjectToCharSequence(_intorfloat(BA.NumberToString(_f))));
RDebugUtils.currentLine=1703940;
 //BA.debugLineNum = 1703940;BA.debugLine="Select STAGE";
switch (BA.switchObjectToInt(_stage,_stage1,_stage3)) {
case 0: {
RDebugUtils.currentLine=1703942;
 //BA.debugLineNum = 1703942;BA.debugLine="num1=f";
_num1 = _f;
 break; }
case 1: {
RDebugUtils.currentLine=1703944;
 //BA.debugLineNum = 1703944;BA.debugLine="num2=f";
_num2 = _f;
 break; }
}
;
RDebugUtils.currentLine=1703946;
 //BA.debugLineNum = 1703946;BA.debugLine="End Sub";
return "";
}
public static String  _buttonsqrt_click() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "buttonsqrt_click", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "buttonsqrt_click", null));}
RDebugUtils.currentLine=1310720;
 //BA.debugLineNum = 1310720;BA.debugLine="Sub ButtonSqrt_Click";
RDebugUtils.currentLine=1310721;
 //BA.debugLineNum = 1310721;BA.debugLine="numResult= Sqrt(num1)";
_numresult = anywheresoftware.b4a.keywords.Common.Sqrt(_num1);
RDebugUtils.currentLine=1310722;
 //BA.debugLineNum = 1310722;BA.debugLine="EditText1.Text=IntorFloat( numResult)";
mostCurrent._edittext1.setText(BA.ObjectToCharSequence(_intorfloat(BA.NumberToString(_numresult))));
RDebugUtils.currentLine=1310723;
 //BA.debugLineNum = 1310723;BA.debugLine="LabelNums.Text=\"√\" & num1 & \"=\" & IntorFloat(numR";
mostCurrent._labelnums.setText(BA.ObjectToCharSequence("√"+BA.NumberToString(_num1)+"="+_intorfloat(BA.NumberToString(_numresult))));
RDebugUtils.currentLine=1310724;
 //BA.debugLineNum = 1310724;BA.debugLine="Select STAGE";
switch (BA.switchObjectToInt(_stage,_stage1,_stage3)) {
case 0: {
RDebugUtils.currentLine=1310726;
 //BA.debugLineNum = 1310726;BA.debugLine="num1=numResult";
_num1 = _numresult;
 break; }
case 1: {
RDebugUtils.currentLine=1310729;
 //BA.debugLineNum = 1310729;BA.debugLine="num2=numResult";
_num2 = _numresult;
 break; }
}
;
RDebugUtils.currentLine=1310731;
 //BA.debugLineNum = 1310731;BA.debugLine="End Sub";
return "";
}
public static String  _buttonsub_click() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "buttonsub_click", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "buttonsub_click", null));}
RDebugUtils.currentLine=917504;
 //BA.debugLineNum = 917504;BA.debugLine="Sub ButtonSub_Click";
RDebugUtils.currentLine=917505;
 //BA.debugLineNum = 917505;BA.debugLine="OPERATION=OPERATION_SUBTRACT";
_operation = _operation_subtract;
RDebugUtils.currentLine=917506;
 //BA.debugLineNum = 917506;BA.debugLine="STAGE=STAGE2";
_stage = _stage2;
RDebugUtils.currentLine=917507;
 //BA.debugLineNum = 917507;BA.debugLine="Label1.Text=\"-\"";
mostCurrent._label1.setText(BA.ObjectToCharSequence("-"));
RDebugUtils.currentLine=917508;
 //BA.debugLineNum = 917508;BA.debugLine="ToastMessageShow(\"-\",False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("-"),anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=917509;
 //BA.debugLineNum = 917509;BA.debugLine="End Sub";
return "";
}
public static String  _buttontheme_click() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "buttontheme_click", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "buttontheme_click", null));}
RDebugUtils.currentLine=1769472;
 //BA.debugLineNum = 1769472;BA.debugLine="Sub ButtonTheme_Click";
RDebugUtils.currentLine=1769473;
 //BA.debugLineNum = 1769473;BA.debugLine="If skin=0 Then";
if (_skin==0) { 
RDebugUtils.currentLine=1769474;
 //BA.debugLineNum = 1769474;BA.debugLine="skin=1";
_skin = (int) (1);
 }else 
{RDebugUtils.currentLine=1769475;
 //BA.debugLineNum = 1769475;BA.debugLine="else if skin=1 Then";
if (_skin==1) { 
RDebugUtils.currentLine=1769476;
 //BA.debugLineNum = 1769476;BA.debugLine="skin=0";
_skin = (int) (0);
 }}
;
RDebugUtils.currentLine=1769478;
 //BA.debugLineNum = 1769478;BA.debugLine="Select skin";
switch (_skin) {
case 0: {
RDebugUtils.currentLine=1769480;
 //BA.debugLineNum = 1769480;BA.debugLine="MakeFlat";
_makeflat();
 break; }
case 1: {
RDebugUtils.currentLine=1769482;
 //BA.debugLineNum = 1769482;BA.debugLine="MakeGradient";
_makegradient();
 break; }
}
;
RDebugUtils.currentLine=1769485;
 //BA.debugLineNum = 1769485;BA.debugLine="End Sub";
return "";
}
public static String  _makeflat() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "makeflat", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "makeflat", null));}
anywheresoftware.b4a.objects.ConcreteViewWrapper _v = null;
int _cr = 0;
anywheresoftware.b4a.objects.drawable.StateListDrawable[] _stddraw = null;
com.halitcan.calc.main._typestate[] _states = null;
int _i = 0;
anywheresoftware.b4a.objects.ButtonWrapper _b = null;
RDebugUtils.currentLine=327680;
 //BA.debugLineNum = 327680;BA.debugLine="Sub MakeFlat";
RDebugUtils.currentLine=327681;
 //BA.debugLineNum = 327681;BA.debugLine="Dim v As View";
_v = new anywheresoftware.b4a.objects.ConcreteViewWrapper();
RDebugUtils.currentLine=327682;
 //BA.debugLineNum = 327682;BA.debugLine="Dim CR As Int=10dip ' CornerRadius";
_cr = anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10));
RDebugUtils.currentLine=327684;
 //BA.debugLineNum = 327684;BA.debugLine="SolidWhite.Initialize2(Colors.White,CR,1,1)";
mostCurrent._solidwhite.Initialize2(anywheresoftware.b4a.keywords.Common.Colors.White,_cr,(int) (1),(int) (1));
RDebugUtils.currentLine=327685;
 //BA.debugLineNum = 327685;BA.debugLine="SolidPink.Initialize2(Colors.ARGB(0,255,0,255),CR";
mostCurrent._solidpink.Initialize2(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (0),(int) (255),(int) (0),(int) (255)),_cr,(int) (1),(int) (1));
RDebugUtils.currentLine=327687;
 //BA.debugLineNum = 327687;BA.debugLine="Dim stdDraw(Activity.NumberOfViews) As StateListD";
_stddraw = new anywheresoftware.b4a.objects.drawable.StateListDrawable[mostCurrent._activity.getNumberOfViews()];
{
int d0 = _stddraw.length;
for (int i0 = 0;i0 < d0;i0++) {
_stddraw[i0] = new anywheresoftware.b4a.objects.drawable.StateListDrawable();
}
}
;
RDebugUtils.currentLine=327688;
 //BA.debugLineNum = 327688;BA.debugLine="Dim states((Activity.NumberOfViews)) As TypeState";
_states = new com.halitcan.calc.main._typestate[(mostCurrent._activity.getNumberOfViews())];
{
int d0 = _states.length;
for (int i0 = 0;i0 < d0;i0++) {
_states[i0] = new com.halitcan.calc.main._typestate();
}
}
;
RDebugUtils.currentLine=327691;
 //BA.debugLineNum = 327691;BA.debugLine="For i =0 To Activity.NumberOfViews-1";
{
final int step7 = 1;
final int limit7 = (int) (mostCurrent._activity.getNumberOfViews()-1);
_i = (int) (0) ;
for (;_i <= limit7 ;_i = _i + step7 ) {
RDebugUtils.currentLine=327692;
 //BA.debugLineNum = 327692;BA.debugLine="v= Activity.GetView(i)";
_v = mostCurrent._activity.GetView(_i);
RDebugUtils.currentLine=327694;
 //BA.debugLineNum = 327694;BA.debugLine="states(i).Initialize";
_states[_i].Initialize();
RDebugUtils.currentLine=327695;
 //BA.debugLineNum = 327695;BA.debugLine="stdDraw(i).Initialize";
_stddraw[_i].Initialize();
RDebugUtils.currentLine=327696;
 //BA.debugLineNum = 327696;BA.debugLine="If v Is Button Then";
if (_v.getObjectOrNull() instanceof android.widget.Button) { 
RDebugUtils.currentLine=327697;
 //BA.debugLineNum = 327697;BA.debugLine="Dim b As Button = v";
_b = new anywheresoftware.b4a.objects.ButtonWrapper();
_b = (anywheresoftware.b4a.objects.ButtonWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ButtonWrapper(), (android.widget.Button)(_v.getObject()));
RDebugUtils.currentLine=327698;
 //BA.debugLineNum = 327698;BA.debugLine="Select v.Tag	'View tags used for styles";
switch (BA.switchObjectToInt(_v.getTag(),(Object)("digit"),(Object)("equal"),(Object)("math"),(Object)("red"),(Object)("yellow"))) {
case 0: {
RDebugUtils.currentLine=327701;
 //BA.debugLineNum = 327701;BA.debugLine="states(i).state2(0)=stdDraw(i).State_Enabled";
_states[_i].state2 /*int[]*/ [(int) (0)] = _stddraw[_i].State_Enabled;
RDebugUtils.currentLine=327702;
 //BA.debugLineNum = 327702;BA.debugLine="states(i).state2(1)= -stdDraw(i).State_Presse";
_states[_i].state2 /*int[]*/ [(int) (1)] = (int) (-_stddraw[_i].State_Pressed);
RDebugUtils.currentLine=327703;
 //BA.debugLineNum = 327703;BA.debugLine="stdDraw(i).AddState2(states(i).state2,SolidWh";
_stddraw[_i].AddState2(_states[_i].state2 /*int[]*/ ,(android.graphics.drawable.Drawable)(mostCurrent._solidwhite.getObject()));
RDebugUtils.currentLine=327705;
 //BA.debugLineNum = 327705;BA.debugLine="states(i).state1=stdDraw(i).State_Pressed";
_states[_i].state1 /*int*/  = _stddraw[_i].State_Pressed;
RDebugUtils.currentLine=327706;
 //BA.debugLineNum = 327706;BA.debugLine="stdDraw(i).AddState(states(i).state1,SolidPin";
_stddraw[_i].AddState(_states[_i].state1 /*int*/ ,(android.graphics.drawable.Drawable)(mostCurrent._solidpink.getObject()));
RDebugUtils.currentLine=327708;
 //BA.debugLineNum = 327708;BA.debugLine="b.Background=stdDraw(i)";
_b.setBackground((android.graphics.drawable.Drawable)(_stddraw[_i].getObject()));
 break; }
case 1: {
RDebugUtils.currentLine=327710;
 //BA.debugLineNum = 327710;BA.debugLine="states(i).state2(0)=stdDraw(i).State_Enabled";
_states[_i].state2 /*int[]*/ [(int) (0)] = _stddraw[_i].State_Enabled;
RDebugUtils.currentLine=327711;
 //BA.debugLineNum = 327711;BA.debugLine="states(i).state2(1)= -stdDraw(i).State_Presse";
_states[_i].state2 /*int[]*/ [(int) (1)] = (int) (-_stddraw[_i].State_Pressed);
RDebugUtils.currentLine=327712;
 //BA.debugLineNum = 327712;BA.debugLine="stdDraw(i).AddState2(states(i).state2,Gradien";
_stddraw[_i].AddState2(_states[_i].state2 /*int[]*/ ,(android.graphics.drawable.Drawable)(mostCurrent._gradientlblue.getObject()));
RDebugUtils.currentLine=327713;
 //BA.debugLineNum = 327713;BA.debugLine="states(i).state1=stdDraw(i).State_Pressed";
_states[_i].state1 /*int*/  = _stddraw[_i].State_Pressed;
RDebugUtils.currentLine=327714;
 //BA.debugLineNum = 327714;BA.debugLine="stdDraw(i).AddState(states(i).state1,Gradient";
_stddraw[_i].AddState(_states[_i].state1 /*int*/ ,(android.graphics.drawable.Drawable)(mostCurrent._gradientred.getObject()));
RDebugUtils.currentLine=327716;
 //BA.debugLineNum = 327716;BA.debugLine="b.Background=stdDraw(i)";
_b.setBackground((android.graphics.drawable.Drawable)(_stddraw[_i].getObject()));
 break; }
case 2: {
RDebugUtils.currentLine=327718;
 //BA.debugLineNum = 327718;BA.debugLine="b.Background=GradientPurple";
_b.setBackground((android.graphics.drawable.Drawable)(mostCurrent._gradientpurple.getObject()));
 break; }
case 3: {
RDebugUtils.currentLine=327721;
 //BA.debugLineNum = 327721;BA.debugLine="b.Background=GradientRed";
_b.setBackground((android.graphics.drawable.Drawable)(mostCurrent._gradientred.getObject()));
 break; }
case 4: {
RDebugUtils.currentLine=327724;
 //BA.debugLineNum = 327724;BA.debugLine="b.Background=GradientYellow";
_b.setBackground((android.graphics.drawable.Drawable)(mostCurrent._gradientyellow.getObject()));
 break; }
}
;
RDebugUtils.currentLine=327729;
 //BA.debugLineNum = 327729;BA.debugLine="b.Invalidate";
_b.Invalidate();
 };
 }
};
RDebugUtils.currentLine=327734;
 //BA.debugLineNum = 327734;BA.debugLine="End Sub";
return "";
}
public static int  _getorientation() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "getorientation", false))
	 {return ((Integer) Debug.delegate(mostCurrent.activityBA, "getorientation", null));}
anywheresoftware.b4a.agraham.reflection.Reflection _r = null;
int _result = 0;
RDebugUtils.currentLine=1572864;
 //BA.debugLineNum = 1572864;BA.debugLine="Sub GetOrientation As Int";
RDebugUtils.currentLine=1572865;
 //BA.debugLineNum = 1572865;BA.debugLine="Dim r As Reflector";
_r = new anywheresoftware.b4a.agraham.reflection.Reflection();
RDebugUtils.currentLine=1572866;
 //BA.debugLineNum = 1572866;BA.debugLine="r.Target = r.GetContext";
_r.Target = (Object)(_r.GetContext(processBA));
RDebugUtils.currentLine=1572867;
 //BA.debugLineNum = 1572867;BA.debugLine="r.Target = r.RunMethod(\"getResources\")";
_r.Target = _r.RunMethod("getResources");
RDebugUtils.currentLine=1572868;
 //BA.debugLineNum = 1572868;BA.debugLine="r.Target = r.RunMethod(\"getConfiguration\")";
_r.Target = _r.RunMethod("getConfiguration");
RDebugUtils.currentLine=1572869;
 //BA.debugLineNum = 1572869;BA.debugLine="Dim Result As Int = r.GetField(\"orientation\")";
_result = (int)(BA.ObjectToNumber(_r.GetField("orientation")));
RDebugUtils.currentLine=1572870;
 //BA.debugLineNum = 1572870;BA.debugLine="Return Result";
if (true) return _result;
RDebugUtils.currentLine=1572872;
 //BA.debugLineNum = 1572872;BA.debugLine="End Sub";
return 0;
}
public static String  _labelmem_click() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "labelmem_click", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "labelmem_click", null));}
RDebugUtils.currentLine=1638400;
 //BA.debugLineNum = 1638400;BA.debugLine="Sub LabelMem_Click";
RDebugUtils.currentLine=1638402;
 //BA.debugLineNum = 1638402;BA.debugLine="End Sub";
return "";
}
}