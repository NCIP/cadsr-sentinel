/* Copyright ScenPro, Inc. 2005
   $Header: /share/content/gforge/sentinel/sentinel/WebRoot/js/run.js,v 1.5 2009-04-17 19:30:08 hebell Exp $
   $Name: not supported by cvs2svn $
*/

    function cmdCancel()
    {
        var objs = document.getElementsByName("nextScreen");
        objs[0].value = "back";
        document.forms["runForm"].submit();
    }

    function cmdHelp()
    {
        window.open(helpUrl, "_blank");
    }
    
    function cmdLogout()
    {
        document.forms["runForm"].action = "/cadsrsentinel/do/logout";
        document.forms["runForm"].submit();
    }

    function cmdSubmit()
    {
        var objs = document.getElementsByName("startDate");
        var sdate = objs[0];
        objs = document.getElementsByName("endDate");
        var edate = objs[0];
        var start = Date.parse(sdate.value);
        var end = Date.parse(edate.value);
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
        	isNaN(temp.getFullYear()))
        {
        	window.alert("Please correct the Start Date.");
            sdate.focus();
        	return;
        }
        sdate.value = (temp.getMonth() + 1) + "/" + temp.getDate() + "/" + temp.getFullYear();
        temp = new Date(end);
        if (isNaN(temp.getMonth()) ||
        	isNaN(temp.getDate()) ||
        	isNaN(temp.getFullYear()))
        {
        	window.alert("Please correct the End Date.");
            edate.focus();
        	return;
        }
        edate.value = (temp.getMonth() + 1) + "/" + temp.getDate() + "/" + temp.getFullYear();
        objs = document.getElementsByName("save1");
        objs[0].disabled = true;
        objs = document.getElementsByName("save2");
        objs[0].disabled = true;
        document.forms["runForm"].submit();
    }

	var defStart = null;
	var defEnd = null;

    function setDates(offset)
    {
        var objs = document.getElementsByName("startDate");
        var sdate = objs[0];
        objs = document.getElementsByName("endDate");
        var edate = objs[0];
        var start = new Date();
        var end = new Date();
        var temp = new Date();
        var mDay = 0;
    	if (defStart === null)
    	{
    		defStart = sdate.value;
    		defEnd = edate.value;
    	}
    	switch (offset)
    	{
    		case 0:
    			sdate.value = defStart;
    			edate.value = defEnd;
    			break;
    		case 1:
		    	temp = new Date();
		    	mDay = 24 * 60 * 60 * 1000;
		    	start = new Date(temp.getTime());
		    	end = new Date(start.getTime() + mDay);
		    	sdate.value = (start.getMonth() + 1) + "/" + start.getDate() + "/" + start.getFullYear();
		    	edate.value = (end.getMonth() + 1) + "/" + end.getDate() + "/" + end.getFullYear();
		    	break;
    		case 2:
		    	temp = new Date();
		    	mDay = 24 * 60 * 60 * 1000;
		    	start = new Date(temp.getTime() - mDay);
		    	end = new Date(start.getTime() + mDay);
		    	sdate.value = (start.getMonth() + 1) + "/" + start.getDate() + "/" + start.getFullYear();
		    	edate.value = (end.getMonth() + 1) + "/" + end.getDate() + "/" + end.getFullYear();
		    	break;
		}
    }
