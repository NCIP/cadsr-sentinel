/* Copyright ScenPro, Inc. 2005
   $Header: /share/content/gforge/sentinel/sentinel/WebRoot/js/create.js,v 1.3 2008-06-20 20:44:30 hebell Exp $
   $Name: not supported by cvs2svn $
*/

    function loaded()
    {
        createForm.propDesc.value = Mdesc[1];
        myName.innerText = Muserid;
        for (var ndx = 0; ndx < createForm.initial.length; ++ndx)
        {
            if (createForm.initial[ndx].checked)
            {
                setBlank(createForm.initial[ndx].value);
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
        createForm.nextScreen.value = "edit";
        createForm.submit();
    }
    
    function cmdCancel()
    {
        createForm.nextScreen.value = "list";
        createForm.submit();
    }
    
    function cmdLogout()
    {
        createForm.action = "/cadsrsentinel/do/logout";
        createForm.submit();
    }
    
    function cmdSave()
    {
        createForm.save1.disabled = true;
        createForm.save2.disabled = true;
        createForm.submit();
    }
    
    function setBlank(val)
    {
        createForm.initial.value = val;
        if (val == "0")
        {
            createForm.save1.disabled = true;
            createForm.save2.disabled = true;
        }
        else
        {
            createForm.save1.disabled = false;
            createForm.save2.disabled = false;
        }
        createForm.propDesc.value = Mdesc[val];
    }

    function saved(val)
    {
        if (val === "")
        {
            createForm.save1.disabled = false;
            createForm.save2.disabled = false;
            return;
        }
        else if (val == "Y")
        {
            alert("Successful save.");
            cmdCancel();
        }
        else
        {
            createForm.save1.disabled = false;
            createForm.save2.disabled = false;
            alert(val);
        }
    }
