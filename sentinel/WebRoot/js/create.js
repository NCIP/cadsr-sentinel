/* Copyright ScenPro, Inc. 2005
   $Header: /share/content/gforge/sentinel/sentinel/WebRoot/js/create.js,v 1.4 2009-04-08 17:56:19 hebell Exp $
   $Name: not supported by cvs2svn $
*/

    function loaded()
    {
        var objs = document.getElementsByName("propDesc");
        objs[0].value = Mdesc[1];
        var obj = document.getElementById("myName");
        obj.innerText = Muserid;
        obj.textContent = Muserid;
        objs = document.getElementsByName("initial");
        obj = objs[0];
        for (var ndx = 0; ndx < obj.length; ++ndx)
        {
            if (obj[ndx].checked)
            {
                setBlank(ndx, obj[ndx].value);
                break;
            }
        }
        loaded2();
    }

    function cmdHelp()
    {
        window.open(helpUrl, "_blank");
    }

    function cmdEdit()
    {
        var objs = document.getElementsByName("nextScreen");
        objs[0].value = "edit";
        document.forms["createForm"].submit();
    }
    
    function cmdCancel()
    {
        var objs = document.getElementsByName("nextScreen");
        objs[0].value = "list";
        document.forms["createForm"].submit();
    }
    
    function cmdLogout()
    {
        document.forms["createForm"].action = "/cadsrsentinel/do/logout";
        document.forms["createForm"].submit();
    }
    
    function cmdSave()
    {
        var objs = document.getElementsByName("save1");
        objs[0].disabled = true;
        objs = document.getElementsByName("save2");
        objs[0].disabled = true;
        document.forms["createForm"].submit();
    }
    
    function setBlank(ndx, val)
    {
        var objs = document.getElementsByName("initial");
        objs[ndx].checked = true;
        objs[ndx].value = val;
        objs = document.getElementsByName("save1");
        var s1 = objs[0];
        objs = document.getElementsByName("save2");
        var s2 = objs[0];
        if (val == "0")
        {
            s1.disabled = true;
            s2.disabled = true;
        }
        else
        {
            s1.disabled = false;
            s2.disabled = false;
        }
        objs = document.getElementsByName("propDesc");
        objs[0].value = Mdesc[val];
    }

    function saved(val)
    {
        var objs = document.getElementsByName("save1");
        var s1 = objs[0];
        objs = document.getElementsByName("save2");
        var s2 = objs[0];
        if (val === "")
        {
            s1.disabled = false;
            s2.disabled = false;
            return;
        }
        else if (val == "Y")
        {
            alert("Successful save.");
            cmdCancel();
        }
        else
        {
            s1.disabled = false;
            s2.disabled = false;
            alert(val);
        }
    }
