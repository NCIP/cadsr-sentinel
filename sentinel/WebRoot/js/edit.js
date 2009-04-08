/* Copyright ScenPro, Inc. 2005
   $Header: /share/content/gforge/sentinel/sentinel/WebRoot/js/edit.js,v 1.5 2009-04-08 17:56:19 hebell Exp $
   $Name: not supported by cvs2svn $
*/

   var MusersTab = null;
   var MmainTab = null;
   var MtabGroup1;

   function setNameList(selObj)
    {
        selObj.length = 0;
        for (var index = 0; index < DBnamesList.length; ++index)
        {
            selObj.options[index] = new Option(DBnamesList[index], DBnamesVals[index]);
        }
    }

    function setGroupList()
    {
        var objs = document.getElementsByName("propUsers");
        var pusers = objs[0];
        pusers.options.length = 0;
        var gText;
        for (var indx2 = 0; indx2 < DBgroupsList.length; ++indx2)
        {
            if (DBgroupsVals[indx2].charAt(0) == "/")
            {
                gText = DBgroupsList[indx2] + " Context Curators";
            }
            else
            {
                gText = "        " + DBgroupsList[indx2];
            }
            pusers.options[indx2] = new Option(gText, DBgroupsVals[indx2]);
        }
    }

    function setEmailList()
    {
        var objs = document.getElementsByName("propUsers");
        var pusers = objs[0];
        setNameList(pusers);
        pusers.options[0] = null;
    }

    function initLists(objOpt, objText, objVal)
    {
        for (var index = 0; index < objText.length; ++index)
        {
            objOpt.options[index] = new Option(objText[index], objVal[index]);
        }
    }

    function initSearchFor()
    {
    }

    function initSearchIn()
    {
    }

    function initSearchAttrs()
    {
        var vFlags = DBsearchAttrsDef;
        for (var index = 0; index < DBsearchAttrsDef.length; ++index)
        {
            vFlags[index] = 1;
        }

        var vAttrs = null;
        var indx2;
        var objs = document.getElementsByName("infoSearchFor");
        var search = objs[0];
        for (index = 0; index < search.options.length; ++index)
        {
            if (search.options[index].selected === true)
            {
                if (vAttrs === null)
                {
                    vAttrs = DBsearchAttrs[index];
                    for (indx2 = 0; indx2 < vFlags.length; ++indx2)
                    {
                        vFlags[indx2] = vAttrs[indx2];
                    }
                }
                else
                {
                    vAttrs = DBsearchAttrs[index];
                    for (indx2 = 0; indx2 < vFlags.length; ++indx2)
                    {
                        vFlags[indx2] = (vAttrs[indx2] == 1 && vFlags[indx2] == 1) ? 1 : 0;
                    }
                }
            }
        }

        indx2 = 0;
        objs = document.getElementsByName("infoSearchIn");
        search = objs[0];
        for (index = 0; index < vFlags.length; ++index)
        {
            if (vFlags[index] == 1)
            {
                search.options[indx2] = new Option(DBsearchInList[index], DBsearchInVals[index]);
                ++indx2;
            }
        }
        for (index = search.options.length - 1; index >= indx2 ; --index)
        {
            search.options[index] = null;
        }
    }

    function initContexts()
    {
        var objs = document.getElementsByName("infoContext");
        initLists(objs[0], DBcontextList, DBcontextVals);
    }

    function initACTypes()
    {
        var objs = document.getElementsByName("infoACTypes");
        initLists(objs[0], DBactypesList, DBactypesVals);
    }

    function initActWorkflow()
    {
        var objs = document.getElementsByName("actWorkflowStatus");
        initLists(objs[0], DBworkflowList, DBworkflowVals);
    }

    function initInfoWorkflow()
    {
        var objs = document.getElementsByName("infoWorkflow");
        initLists(objs[0], DBcworkflowList, DBcworkflowVals);
    }

    function initActRegStatus()
    {
        var objs = document.getElementsByName("actRegStatus");
        initLists(objs[0], DBregStatusList, DBregStatusVals);
    }

    function initInfoRegStatus()
    {
        var objs = document.getElementsByName("infoRegStatus");
        initLists(objs[0], DBregCStatusList, DBregCStatusVals);
    }

    function initRepAttributes()
    {
    }

    function sortDisplayList()
    {
        var objs = document.getElementsByName("repAttributes");
        var attrs = objs[0];
        var loop = true;
        while (loop)
        {
            loop = false;
            for (var index = 1; index < (DBdisplayList.length - 1); ++index)
            {
                if (DBdisplayList[index] > DBdisplayList[index + 1])
                {
                    var tList = DBdisplayList[index];
                    DBdisplayList[index] = DBdisplayList[index + 1];
                    DBdisplayList[index + 1] = tList;
                    var tVals = DBdisplayVals[index];
                    DBdisplayVals[index] = DBdisplayVals[index + 1];
                    DBdisplayVals[index + 1] = tVals;

                    var rsel0 = attrs.options[index].selected;
                    var rsel1 = attrs.options[index + 1].selected;
                    tList = attrs.options[index].text;
                    attrs.options[index].text = attrs.options[index + 1].text;
                    attrs.options[index + 1].text = tList;
                    tVals = attrs.options[index].value;
                    attrs.options[index].value = attrs.options[index + 1].value;
                    attrs.options[index + 1].value = tVals;
                    attrs.options[index].selected = rsel1;
                    attrs.options[index + 1].selected = rsel0;

                    for (var indx2 = 0; indx2 < DBdisplayAttrs.length; ++indx2)
                    {
                        var vAttrs = DBdisplayAttrs[indx2];
                        var tAttrs = vAttrs[index];
                        vAttrs[index] = vAttrs[index + 1];
                        vAttrs[index + 1] = tAttrs;
                    }
                    loop = true;
                }
            }
        }
    }

    function initDisplayAttrs()
    {
        var vFlags = DBdisplayAttrsDef;
        for (var index = 0; index < DBdisplayAttrsDef.length; ++index)
        {
            vFlags[index] = 1;
        }

        var objs = document.getElementsByName("infoSearchFor");
        var sel = objs[0];
        var vAttrs = null;
        var indx2;
        for (index = 0; index < sel.options.length; ++index)
        {
            if (sel.options[index].selected === true)
            {
                if (vAttrs === null)
                {
                    vAttrs = DBdisplayAttrs[index];
                    for (indx2 = 0; indx2 < vFlags.length; ++indx2)
                    {
                        vFlags[indx2] = vAttrs[indx2];
                    }
                }
                else
                {
                    vAttrs = DBdisplayAttrs[index];
                    for (indx2 = 0; indx2 < vFlags.length; ++indx2)
                    {
                        vFlags[indx2] = (vAttrs[indx2] == 1 || vFlags[indx2] == 1) ? true : false;
                    }
                }
            }
        }
        indx2 = 0;
        objs = document.getElementsByName("repAttributes");
        sel = objs[0];
        for (index = 0; index < vFlags.length; ++index)
        {
            if (vFlags[index] == 1)
            {
                sel.options[indx2] = new Option(DBdisplayList[index], DBdisplayVals[index]);
                ++indx2;
            }
        }
        for (index = sel.options.length - 1; index >= indx2 ; --index)
        {
            sel.options[index] = null;
        }
        sel.options[0].selected = true;
    }

    function initSchemes()
    {
        var objs = document.getElementsByName("infoSchemes");
        initLists(objs[0], DBschemeList, DBschemeVals);
    }

    function initProtos()
    {
        var objs = document.getElementsByName("infoProtos");
        initLists(objs[0], DBprotoList, DBprotoVals);
    }

    function initSchemeItems()
    {
        var objs = document.getElementsByName("infoSchemeItems");
        initLists(objs[0], DBschemeItemList, DBschemeItemVals);
    }

    function initForms()
    {
        var objs = document.getElementsByName("infoForms");
        initLists(objs[0], DBformsList, DBformsVals);
    }

    function initConceptDomain()
    {
    }

    function initRecipients()
    {
        var objs = document.getElementsByName("propRecipients");
        var recips = objs[0];
        var ndx3 = 0;
        var ndx2;
        var gText;
        recips.options.length = 0;
        for (var ndx = 0; ndx < DBrecipients.length; ++ndx)
        {
            // Allow for freeform Email and URL entries
            if ((DBrecipients[ndx].indexOf("@") > -1) || (DBrecipients[ndx].indexOf("https://") > -1) || (DBrecipients[ndx].indexOf("http://") > -1))
            {
                recips.options[ndx3] = new Option(DBrecipients[ndx], DBrecipients[ndx]);
                ++ndx3;
            }

            // Allow for Context Curator groups
            else if (DBrecipients[ndx].charAt(0) == "/")
            {
                for (ndx2 = 0; ndx2 < DBgroupsList.length; ++ndx2)
                {
                    if (DBrecipients[ndx] == DBgroupsVals[ndx2])
                    {
                        gText = DBgroupsList[ndx2] + " Context Curators";
                        recips.options[ndx3] = new Option(gText, DBgroupsVals[ndx2]);
                        ++ndx3;
                        break;
                    }
                }
            }

            // All others must be individual user accounts.
            else
            {
                for (ndx2 = 0; ndx2 < DBnamesVals.length; ++ndx2)
                {
                    if (DBrecipients[ndx] == DBnamesVals[ndx2])
                    {
                        recips.options[ndx3] = new Option(DBnamesList[ndx2], DBnamesVals[ndx2]);
                        ++ndx3;
                        break;
                    }
                }
            }
        }
    }

    function loaded()
    {
        var tProp = document.getElementById("tabProp");
        var tReport = document.getElementById("tabReport");
        var tQual = document.getElementById("tabQual");
        var tMon = document.getElementById("tabMon");
        MtabGroup1 = [
            [document.getElementById("tabMain1"), tProp],
            [document.getElementById("tabMain2"), tReport],
            [document.getElementById("tabMain3"), tQual],
            [document.getElementById("tabMain4"), tMon]
        ];

        tProp.style.position = "absolute";
        tProp.style.visibility = "visible";
        tMon.style.position = "absolute";
        tMon.style.top = tProp.style.top;
        tMon.style.left = tProp.style.left;
        tMon.style.visibility = "hidden";
        tReport.style.position = "absolute";
        tReport.style.top = tProp.style.top;
        tReport.style.left = tProp.style.left;
        tReport.style.visibility = "hidden";
        tQual.style.visibility = "hidden";

        MusersTab = document.getElementById("propUList");
        var objs = document.getElementsByName("usersTab");
        if (objs[0].value != "0")
        {
            selectTab0(document.getElementById("propGList"));
        }
        else
        {
            setEmailList();
        }
        MmainTab = document.getElementById("tabMain1");
        objs = document.getElementsByName("mainTab");
        if (objs[0].value != "0")
        {
            selectTab1(MtabGroup1[objs[0].value][0]);
        }
        initContexts();
        initActWorkflow();
        initActRegStatus();
        initInfoRegStatus();
        initInfoWorkflow();
        initACTypes();
        objs = document.getElementsByName("infoCreator");
        setNameList(objs[0]);
        objs = document.getElementsByName("infoModifier");
        setNameList(objs[0]);
        initRecipients();
        loaded0();
        objs = document.getElementsByName("actWorkflowStatus");
        objs[0].options[0] = null;
        objs = document.getElementsByName("actRegStatus");
        objs[0].options[0] = null;

        objs = document.getElementsByName("actVersion");
        if (objs[3].checked)
        {
            objs = document.getElementsByName("actVerNum");
            objs[0].value = DBactVerNum;
            objs[0].disabled = false;
        }
        else
        {
            objs = document.getElementsByName("actVerNum");
            objs[0].value = "";
        }

        // Update the sample display.
        setToSample();
        objs = document.getElementsByName("propName");
        nameChanged(objs[0].value);
        objs = document.getElementsByName("save1");
        objs[0].disabled = false;
        objs = document.getElementsByName("save2");
        objs[0].disabled = false;
        saveCheck();
    }

    function setActVerNum(value)
    {
        var objs = document.getElementsByName("actVerNum");
        var vn = objs[0];
        if (value == "S")
        {
            vn.disabled = false;
            vn.value = DBactVerNum;
        }
        else
        {
            if (vn.value !== "")
            {
                DBactVerNum = vn.value;
            }
            vn.value = "";
            vn.disabled = true;
        }
    }

    function setFreq(value)
    {
        var objs = document.getElementsByName("freqWeekly");
        var fw = objs[0];
        objs = document.getElementsByName("freqMonthly");
        var fm = objs[0];
        if (value == "D")
        {
            fw.disabled = true;
            fm.disabled = true;
        }
        else if (value == "W")
        {
            fw.disabled = false;
            fm.disabled = true;
        }
        else
        {
            fw.disabled = true;
            fm.disabled = false;
        }
    }

    function nameChanged(value)
    {
    }

    function addFEmail()
    {
        var objs = document.getElementsByName("propEmail");
        var pemail = objs[0];
        if (pemail.value !== null && pemail.value !== "")
        {
            if ((pemail.value.indexOf("@") == -1) && (pemail.value.indexOf("https://") !== 0 && pemail.value.indexOf("http://") !== 0))
            {
                window.alert("The email address or URL is not correctly formed, add '@', 'https://' or 'http://' as desired. The URL prefix is case sensitive.");
                pemail.focus();
                return;
            }
            if (findChosenText(pemail.value) === false)
            {
                objs = document.getElementsByName("propRecipients");
                var index = objs[0].options.length;
                objs[0].options[index] = new Option(pemail.value, pemail.value);
                setToSample();
            }
            pemail.value = "";
            pemail.focus();
        }
    }

    function setToSample()
    {
        var objs = document.getElementsByName("propRecipients");
        var recips = objs[0];
        var cstr;
        if (recips.options.length > 0)
        {
            cstr = recips.options[0].text;
            for (var index = 1; index < recips.options.length; ++index)
            {
                cstr = cstr + ", " + recips.options[index].text;
            }
        }
        else
        {
            cstr = "";
        }
        var addr = document.getElementById("sampleAddr");
        addr.innerText = cstr;
        addr.textContent = cstr;
    }

    function findChosenText(value)
    {
        var objs = document.getElementsByName("propRecipients");
        for (var indx2 = 0; indx2 < objs[0].options.length; ++indx2)
        {
            if (objs[0].options[indx2].text == value)
            {
                return true;
            }
        }
        return false;
    }

    function findChosenValue(value)
    {
        var objs = document.getElementsByName("propRecipients");
        for (var indx2 = 0; indx2 < objs[0].options.length; ++indx2)
        {
            if (objs[0].options[indx2].value == value)
            {
                return objs[0].options[indx2].text;
            }
        }
        return null;
    }

    function addToEmail()
    {
        addToEmailUsers();
    }

    function addToEmailUsers()
    {
        var objs = document.getElementsByName("propRecipients");
        var recips = objs[0];
        objs = document.getElementsByName("propUsers");
        var users = objs[0];
        var insert = recips.options.length;
        for (var index = 0; index < users.options.length; ++index)
        {
            if (users.options[index].selected === true)
            {
                users.options[index].selected = false;
                if (findChosenValue(users.options[index].value) === null)
                {
                    var pObj = users.options[index];
                    recips.options[insert] = new Option(pObj.text, pObj.value);
                    ++insert;
                }
            }
        }
        setToSample();
    }

    function removeFromEmail()
    {
        var objs = document.getElementsByName("propRecipients");
        var recips = objs[0];
        for (var index = recips.options.length - 1; index >= 0; --index)
        {
            if (recips.options[index].selected === true)
            {
                objs = document.getElementsByName("creatorID");
                if (objs[0].value == recips.options[index].value)
                {
                    alert("The Creator can not be removed from the recipient list.");
                    recips.options[index].selected = false;
                }
                else
                {
                    recips.options[index] = null;
                }
            }
        }
        setToSample();
    }

    function clearSelected(selObj)
    {
        for (var index = 0; index < selObj.options.length; ++index)
        {
            selObj.options[index].selected = false;
        }
    }

    function setSelected(sobj, vals)
    {
        sobj.options[0].selected = false;
        for (var index = 0; index < sobj.options.length; ++index)
        {
            for (var index2 = 0; index2 < vals.length; ++index2)
            {
                if (sobj.options[index].value == vals[index2])
                {
                    sobj.options[index].selected = true;
                    break;
                }
            }
        }
        if (sobj.selectedIndex == -1)
        {
            sobj.options[0].selected = true;
        }
    }

    function setDependant(field, tobj, list, vals, cvals)
    {
        var selCnt = 0;
        var selVals = new Array();
        for (var index = 1; index < field.options.length; ++index)
        {
            if (field.options[index].selected === true)
            {
                selVals[selCnt++] = field.options[index].value;
            }
        }
        tobj.options.length = 0;
        var index3 = 0;
        tobj.options[index3] = new Option(list[0], vals[0]);
        for (var index2 = 1; index2 < cvals.length; ++index2)
        {
            for (index = 0; index < selVals.length; ++index)
            {
                if (selVals[index] == cvals[index2])
                {
                    ++index3;
                    tobj.options[index3] = new Option(list[index2], vals[index2]);
                    break;
                }
            }
        }
        tobj.options[0].selected = true;
    }

    function changedCS()
    {
        var objs = document.getElementsByName("infoSchemes");
        var schemes = objs[0];
        objs = document.getElementsByName("infoSchemeItems");
        var items = objs[0];
        if (schemes.options[0].selected === true)
        {
            initSchemeItems();
            items.options[0].selected = true;
        }
        else
        {
            setDependant(schemes, items, DBschemeItemList, DBschemeItemVals, DBschemeItemSchemes);
        }
    }

    function changedContext()
    {
        var objs = document.getElementsByName("infoContext");
        var cont = objs[0];
        objs = document.getElementsByName("infoSchemes");
        var schemes = objs[0];
        objs = document.getElementsByName("infoForms");
        var forms = objs[0];
        objs = document.getElementsByName("infoProtos");
        var protos = objs[0];
        if (cont.options[0].selected === true)
        {
            initConceptDomain();
            initSchemes();
            initProtos();
            initForms();
//            editForm.infoConceptDomain.options[0].selected = true;
            schemes.options[0].selected = true;
            forms.options[0].selected = true;
            protos.options[0].selected = true;
            changedCS();
        }
        else
        {
//            setDependant(cont, editForm.infoConceptDomain, DBconceptList, DBconceptVals, DBconceptContexts);
            setDependant(cont, schemes, DBschemeList, DBschemeVals, DBschemeContexts);
            setDependant(cont, protos, DBprotoList, DBprotoVals, DBprotoContexts);
            setDependant(cont, forms, DBformsList, DBformsVals, DBformsContexts);
            changedCS();
        }
    }

    function fixListAllSpecials(selObj2, optndx)
    {
        clearSelected(selObj2);
        selObj2.options[optndx].selected = true;
    }

    function fixListAll(selObj)
    {
        var objs;
        if (selObj.selectedIndex == -1)
        {
            selObj.options[0].selected = true;
        }
        if (selObj.options[0].selected === true)
        {
            fixListAllSpecials(selObj, 0);
        }
        else
        {
            if (selObj.name == "infoRegStatus")
            {
                objs = document.getElementsByName("actRegStatus");
                fixListAllSpecials(objs[0], 0);
            }
            else if (selObj.name == "infoWorkflow")
            {
                objs = document.getElementsByName("actWorkflowStatus");
                fixListAllSpecials(objs[0], 0);
            }
        }
        if (selObj.name == "infoSearchFor")
        {
//            changedAC();
        }
        else if (selObj.name == "infoContext")
        {
            changedContext();
        }
        else if (selObj.name == "infoSchemes")
        {
            changedCS();
        }
    }

    function fixXStatus(selObj)
    {
        var objs;
        if (selObj.selectedIndex == -1)
        {
            selObj.options[0].selected = true;
        }
        if (selObj.options[0].selected === true)
        {
            fixListAllSpecials(selObj, 0);
        }
        else if (selObj.options[1].selected === true)
        {
            fixListAllSpecials(selObj, 1);
        }
        else
        {
            if (selObj.name == "actRegStatus")
            {
                objs = document.getElementsByName("infoRegStatus");
                fixListAllSpecials(objs[0], 0);
            }
            else if (selObj.name == "actWorkflowStatus")
            {
                objs = document.getElementsByName("infoWorkflow");
                fixListAllSpecials(objs[0], 0);
            }
        }
    }

    function disableInfoWorkflowStatus(flag, vis)
    {
    }

    function disableInfoRegStatus(flag, vis)
    {
    }

    function disableInfoSchemes(flag, vis)
    {
        var objs = document.getElementsByName("infoSchemes");
        objs[0].disabled = flag;
    }

    function disableInfoVDT(flag, vis)
    {
    }

    function disableInfoCreator(flag, vis)
    {
        var objs = document.getElementsByName("infoCreator");
        objs[0].disabled = flag;
    }

    function disableInfoModifier(flag, vis)
    {
        var objs = document.getElementsByName("infoModifier");
        objs[0].disabled = flag;
    }

    function disableInfoVersion(flag, vis)
    {
        var objs = document.getElementsByName("actVersion");
        objs[0].disabled = flag;
        objs[1].disabled = flag;
        objs[2].disabled = flag;
        objs[3].disabled = flag;
        if (flag)
        {
            objs = document.getElementsByName("actVerNum");
            objs[0].disabled = true;
        }
        else
        {
            if (objs[3].checked === true)
            {
                objs = document.getElementsByName("actVerNum");
                objs[0].disabled = false;
            }
        }
    }

    function disableInfoContextUse(flag, vis)
    {
    }

    function disableInfoSearchIn(flag, vis)
    {
    }

    function disableInfoConceptDomain(flag, vis)
    {
    }

    var funcTable = [disableInfoWorkflowStatus, disableInfoRegStatus, disableInfoSchemes,
        disableInfoVDT, disableInfoCreator, disableInfoModifier, disableInfoContextUse,
        disableInfoVersion, disableInfoSearchIn, disableInfoConceptDomain];

    var funcTableFlags = [
        [0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
        [1, 1, 0, 1, 1, 1, 1, 1, 0, 1],
        [0, 1, 1, 1, 0, 0, 1, 0, 0, 1],
        [0, 1, 1, 1, 1, 1, 1, 1, 1, 1],
        [0, 0, 1, 1, 0, 0, 0, 0, 0, 1],
        [0, 1, 1, 1, 0, 0, 1, 0, 0, 1],
        [0, 1, 1, 0, 0, 0, 1, 0, 0, 1],
        [0, 1, 1, 1, 1, 1, 1, 1, 0, 1],
        [1, 1, 1, 1, 1, 1, 1, 1, 0, 0],
        [0, 1, 1, 1, 1, 1, 1, 1, 0, 1],
        [1, 1, 1, 1, 0, 0, 0, 0, 0, 1]];

    function enableFunctions(flagVector)
    {
        for (var indx2 = 0; indx2 < funcTable.length; ++indx2)
        {
            var bDisable = (flagVector[indx2] == 1) ? true : false;
            var tVisible = (bDisable) ? "hidden" : "visible";
            funcTable[indx2](bDisable, tVisible);
        }
    }

    function changedAC2()
    {
        var flagVector = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0];
        var tVect = null;
        var objs = document.getElementsByName("infoSearchFor");
        var sel = objs[0];
        var indx2;
        if (sel.options[0].selected === false)
        {
            for (var index = 1; index < sel.options.length; ++index)
            {
                if (sel.options[index].selected === true)
                {
                    if (tVect === null)
                    {
                        tVect = funcTableFlags[index];
                        for (indx2 = 0; indx2 < flagVector.length; ++indx2)
                        {
                            flagVector[indx2] = tVect[indx2];
                        }
                    }
                    else
                    {
                        tVect = funcTableFlags[index];
                        for (indx2 = 0; indx2 < flagVector.length; ++indx2)
                        {
                            flagVector[indx2] = (flagVector[indx2] == 1 && tVect[indx2] == 1) ? 1 : 0;
                        }
                    }
                }
            }
        }
        enableFunctions(flagVector);
    }

    function changedAC()
    {
        changedAC2();
    }

    function tabMouseOut(obj)
    {
        obj.className = (obj == MusersTab || obj == MmainTab) ? "tab0" : "tab1";
    }

    function tabMouseOver(obj)
    {
        obj.className = (obj == MusersTab || obj == MmainTab) ? "tab0" : "tab2";
    }

    function selectTab0(obj)
    {
        obj.className = "tab0";
        MusersTab.className = "tab1";
        MusersTab = obj;
        var objs = document.getElementsByName("usersTab");
        if (obj == document.getElementById("propUList"))
        {
            setEmailList();
            objs[0].value = "0";
        }
        else
        {
            setGroupList();
            objs[0].value = "1";
        }
    }

    function selectTab1(obj)
    {
        MmainTab.className = "tab1";
        obj.className = "tab0";
        for (var ndx = 0; ndx < MtabGroup1.length; ++ndx)
        {
            var tab = MtabGroup1[ndx];
            if (tab[0] == MmainTab)
            {
                tab[1].style.visibility = "hidden";
                break;
            }
        }
        MmainTab = obj;
        for (ndx = 0; ndx < MtabGroup1.length; ++ndx)
        {
            tab = MtabGroup1[ndx];
            if (tab[0] == MmainTab)
            {
                tab[1].style.visibility = "visible";
                var objs = document.getElementsByName("mainTab");
                objs[0].value = ndx;
                break;
            }
        }
    }

    function cmdHelp()
    {
        window.open(helpUrl, "_blank");
    }

    function cmdSave()
    {
        var objs;
        objs = document.getElementsByName("propBeginDate");
        var bdate = objs[0];
        var beginDate = Date.parse(bdate.value);
        objs = document.getElementsByName("propEndDate");
        var edate = objs[0];
        var endDate = Date.parse(edate.value);

        var tmain = document.getElementById("tabMain1");
        objs = document.getElementsByName("actVerNum");
        var vnum = objs[0];
        objs = document.getElementsByName("propName");
        var pnam = objs[0];
        objs = document.getElementsByName("propStatus");
        var psta = objs[0];
        objs = document.getElementsByName("propStatusReason");
        var pstr = objs[0];
        objs = document.getElementsByName("infoForms");
        var forms = objs[0];
        objs = document.getElementsByName("infoSchemes");
        var schemes = objs[0];
        objs = document.getElementsByName("infoSchemeItems");
        var items = objs[0];
		if (vnum.value.search(/[^0-9.]/g) > -1)
		{
            selectTab1(document.getElementById("tabMain4"));
            vnum.focus();
            alert("A Version number can only contain digits and a period.");
		}
        else if (pnam.value === "")
        {
            selectTab1(tmain);
            pnam.focus();
            alert("A Sentinel Name must be provided.");
        }
        else if (psta[3].checked === true && MstatusReason === false)
        {
            selectTab1(tmain);
            pstr.value = "(please explain why this is inactive)";
            pstr.focus();
            alert("A reason must be given when the Status is set to Inactive.");
        }
        else if (psta[2].checked === true && bdate.value === "" && edate.value === "")
        {
            selectTab1(tmain);
            bdate.focus();
            alert("A begin and/or end date must be given when the Status is set to Active with Dates.");
        }
        else if (bdate.value !== "" && isNaN(beginDate) === true)
        {
            selectTab1(tmain);
            bdate.focus();
            alert("The begin date is incorrect.");
        }
        else if (edate.value !== "" && isNaN(endDate) === true)
        {
            selectTab1(tmain);
            edate.focus();
            alert("The end date is incorrect.");
        }
        else if (isNaN(beginDate) === false && isNaN(endDate) === false && endDate < beginDate)
        {
            selectTab1(tmain);
            bdate.focus();
            alert("The begin date must preceed the end date.");
        }
        else if (forms.options[0].selected === false &&
            (schemes.options[0].selected === false || items.options[0].selected === false))
        {
            selectTab1(document.getElementById("tabMain3"));
            alert("Classification Schemes/Items and Forms can not both be selected for criteria.  Please change one or both to (All).");
        }
        else // We think the form is good to go.
        {
            objs = document.getElementsByName("propRecipients");
            var recips = objs[0];
            for (var ndx = 0; ndx < recips.options.length; ++ndx)
            {
                recips.options[ndx].selected = true;
            }
            objs = document.getElementsByName("nextScreen");
            objs[0].value = "save";
            objs = document.getElementsByName("save1");
            objs[0].disabled = true;
            objs = document.getElementsByName("save2");
            objs[0].disabled = true;
            document.forms["editForm"].submit();
        }
    }

    function cmdClear()
    {
        document.forms["editForm"].submit();
    }

    function cmdRun()
    {
        var objs = document.getElementsByName("actVerNum");
		if (objs[0].value.search(/[^0-9.]/g) > -1)
		{
            selectTab1(document.getElementById("tabMain4"));
            objs[0].focus();
            alert("A Version number can only contain digits and a period.");
		}
		else
		{
            objs = document.getElementsByName("propRecipients");
            var recips = objs[0];
	        for (var ndx = 0; ndx < recips.options.length; ++ndx)
            {
	            recips.options[ndx].selected = true;
            }
            objs = document.getElementsByName("nextScreen");
	        objs[0].value = "run";
	        document.forms["editForm"].submit();
	    }
    }

    function cmdBack()
    {
        var objs = document.getElementsByName("nextScreen");
        objs[0].value = "back";
        document.forms["editForm"].submit();
    }

    function cmdLogout()
    {
        document.forms["editForm"].action = "/cadsrsentinel/do/logout";
        document.forms["editForm"].submit();
    }

    function saved(val)
    {
        if (val == "Y")
        {
            alert("Successful save.");
            var objs = document.getElementsByName("nextScreen");
            objs[0].value = "list";
            document.forms["editForm"].submit();
        }
        else
        {
            alert(val);
        }
    }

    function checkReason(val)
    {
        MstatusReason = (val !== null && val !== "");
    }

    function highlightReason()
    {
        var objs = document.getElementsByName("propStatusReason");
        objs[0].select();
    }
    