/* Copyright ScenPro, Inc. 2005
   $Header: /share/content/gforge/sentinel/sentinel/WebRoot/js/run.js,v 1.2 2007-09-25 14:26:46 hebell Exp $
   $Name: not supported by cvs2svn $
*/

    function cmdCancel()
    {
        runForm.nextScreen.value = "back";
        runForm.submit();
    }

    function cmdHelp()
    {
        window.open("/cadsrsentinel/html/help.html", "_blank");
    }
    
    function cmdLogout()
    {
        runForm.action = "/cadsrsentinel/do/logout";
        runForm.submit();
    }

    function cmdSubmit()
    {
        var start = Date.parse(runForm.startDate.value);
        var end = Date.parse(runForm.endDate.value);
        var temp = start;
        if (start > end)
        {
            temp = start;
            start = end;
            end = temp;
        }
        temp = new Date(start);
        if (isNaN(temp.getMonth()) ||
        	isNaN(temp.getDate()) ||
        	isNaN(temp.getYear()))
        {
        	window.alert("Please correct the Start Date.");
            runForm.startDate.focus();
        	return;
        }
        runForm.startDate.value = (temp.getMonth() + 1) + "/" + temp.getDate() + "/" + temp.getYear();
        temp = new Date(end);
        if (isNaN(temp.getMonth()) ||
        	isNaN(temp.getDate()) ||
        	isNaN(temp.getYear()))
        {
        	window.alert("Please correct the End Date.");
            runForm.endDate.focus();
        	return;
        }
        runForm.endDate.value = (temp.getMonth() + 1) + "/" + temp.getDate() + "/" + temp.getYear();
        runForm.save1.disabled = true;
        runForm.save2.disabled = true;
        runForm.submit();
    }

	var defStart = null;
	var defEnd = null;

    function setDates(offset)
    {
        var start = new Date();
        var end = new Date();
        var temp = new Date();
        var mDay = 0;
    	if (defStart === null)
    	{
    		defStart = runForm.startDate.value;
    		defEnd = runForm.endDate.value;
    	}
    	switch (offset)
    	{
    		case 0:
    			runForm.startDate.value = defStart;
    			runForm.endDate.value = defEnd;
    			break;
    		case 1:
		    	temp = new Date();
		    	mDay = 24 * 60 * 60 * 1000;
		    	start = new Date(temp.getTime());
		    	end = new Date(start.getTime() + mDay);
		    	runForm.startDate.value = (start.getMonth() + 1) + "/" + start.getDate() + "/" + start.getYear();
		    	runForm.endDate.value = (end.getMonth() + 1) + "/" + end.getDate() + "/" + end.getYear();
		    	break;
    		case 2:
		    	temp = new Date();
		    	mDay = 24 * 60 * 60 * 1000;
		    	start = new Date(temp.getTime() - mDay);
		    	end = new Date(start.getTime() + mDay);
		    	runForm.startDate.value = (start.getMonth() + 1) + "/" + start.getDate() + "/" + start.getYear();
		    	runForm.endDate.value = (end.getMonth() + 1) + "/" + end.getDate() + "/" + end.getYear();
		    	break;
		}
    }
