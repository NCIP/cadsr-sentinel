/* Copyright ScenPro, Inc. 2005
   $Header: /share/content/gforge/sentinel/sentinel/WebRoot/js/edit.js,v 1.2 2007-09-25 14:26:46 hebell Exp $
   $Name: not supported by cvs2svn $
*/

   function setNameList(selObj)
    {
        selObj.length = 0;
        for (index = 0; index < DBnamesList.length; ++index)
        {
            selObj.options[index] = new Option(DBnamesList[index], DBnamesVals[index]);
        }
    }

    function setGroupList()
    {
        editForm.propUsers.length = 0;
        for (indx2 = 0; indx2 < DBgroupsList.length; ++indx2)
        {
            if (DBgroupsVals[indx2].charAt(0) == "/")
            {
                gText = DBgroupsList[indx2] + " Context Curators";
            }
            else
            {
                gText = "        " + DBgroupsList[indx2];
            }
            editForm.propUsers.options[indx2] = new Option(gText, DBgroupsVals[indx2]);
        }
    }
    
    function setEmailList()
    {
        setNameList(editForm.propUsers);
        editForm.propUsers.options[0] = null;
    }

    function initLists(objOpt, objText, objVal)
    {
        for (index = 0; index < objText.length; ++index)
        {
            objOpt.options[index] = new Option(objText[index], objVal[index]);
        }
    }

    function initSearchFor()
    {
//        initLists(editForm.infoSearchFor, DBsearchList, DBsearchVals);
    }

    function initSearchIn()
    {
//        initLists(editForm.infoSearchIn, DBsearchInList, DBsearchInVals);
    }

    function initSearchAttrs()
    {
        vFlags = DBsearchAttrsDef;
        for (index = 0; index < DBsearchAttrsDef.length; ++index)
        {
            vFlags[index] = 1;
        }

        vAttrs = null;
        for (index = 0; index < editForm.infoSearchFor.options.length; ++index)
        {
            if (editForm.infoSearchFor.options[index].selected === true)
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
        for (index = 0; index < vFlags.length; ++index)
        {
            if (vFlags[index] == 1)
            {
                editForm.infoSearchIn.options[indx2] = new Option(DBsearchInList[index], DBsearchInVals[index]);
                ++indx2;
            }
        }
        for (index = editForm.infoSearchIn.options.length - 1; index >= indx2 ; --index)
        {
            editForm.infoSearchIn.options[index] = null;
        }
    }

    function initContexts()
    {
        initLists(editForm.infoContext, DBcontextList, DBcontextVals);
    }

    function initACTypes()
    {
        initLists(editForm.infoACTypes, DBactypesList, DBactypesVals);
    }

    function initActWorkflow()
    {
        initLists(editForm.actWorkflowStatus, DBworkflowList, DBworkflowVals);
    }

    function initInfoWorkflow()
    {
        initLists(editForm.infoWorkflow, DBcworkflowList, DBcworkflowVals);
    }

    function initActRegStatus()
    {
        initLists(editForm.actRegStatus, DBregStatusList, DBregStatusVals);
    }

    function initInfoRegStatus()
    {
        initLists(editForm.infoRegStatus, DBregCStatusList, DBregCStatusVals);
    }

    function initRepAttributes()
    {
//        initLists(editForm.repAttributes, DBdisplayList, DBdisplayVals);
    }

    function sortDisplayList()
    {
        loop = true;
        while (loop)
        {
            loop = false;
            for (index = 1; index < (DBdisplayList.length - 1); ++index)
            {
                if (DBdisplayList[index] > DBdisplayList[index + 1])
                {
                    tList = DBdisplayList[index];
                    DBdisplayList[index] = DBdisplayList[index + 1];
                    DBdisplayList[index + 1] = tList;
                    tVals = DBdisplayVals[index];
                    DBdisplayVals[index] = DBdisplayVals[index + 1];
                    DBdisplayVals[index + 1] = tVals;
                    
                    rsel0 = editForm.repAttributes.options[index].selected;
                    rsel1 = editForm.repAttributes.options[index + 1].selected;
                    tList = editForm.repAttributes.options[index].text;
                    editForm.repAttributes.options[index].text = editForm.repAttributes.options[index + 1].text;
                    editForm.repAttributes.options[index + 1].text = tList;
                    tVals = editForm.repAttributes.options[index].value;
                    editForm.repAttributes.options[index].value = editForm.repAttributes.options[index + 1].value;
                    editForm.repAttributes.options[index + 1].value = tVals;
                    editForm.repAttributes.options[index].selected = rsel1;
                    editForm.repAttributes.options[index + 1].selected = rsel0;

                    for (indx2 = 0; indx2 < DBdisplayAttrs.length; ++indx2)
                    {
                        vAttrs = DBdisplayAttrs[indx2];
                        tAttrs = vAttrs[index];
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
        vFlags = DBdisplayAttrsDef;
        for (index = 0; index < DBdisplayAttrsDef.length; ++index)
        {
            vFlags[index] = 1;
        }

        vAttrs = null;
        for (index = 0; index < editForm.infoSearchFor.options.length; ++index)
        {
            if (editForm.infoSearchFor.options[index].selected === true)
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
        for (index = 0; index < vFlags.length; ++index)
        {
            if (vFlags[index] == 1)
            {
                editForm.repAttributes.options[indx2] = new Option(DBdisplayList[index], DBdisplayVals[index]);
                ++indx2;
            }
        }
        for (index = editForm.repAttributes.options.length - 1; index >= indx2 ; --index)
        {
            editForm.repAttributes.options[index] = null;
        }
        editForm.repAttributes.options[0].selected = true;
    }

    function initSchemes()
    {
        initLists(editForm.infoSchemes, DBschemeList, DBschemeVals);
    }

    function initProtos()
    {
        initLists(editForm.infoProtos, DBprotoList, DBprotoVals);
    }

    function initSchemeItems()
    {
        initLists(editForm.infoSchemeItems, DBschemeItemList, DBschemeItemVals);
    }

    function initForms()
    {
        initLists(editForm.infoForms, DBformsList, DBformsVals);
    }

    function initConceptDomain()
    {
//        initLists(editForm.infoConceptDomain, DBconceptList, DBconceptVals);
    }

    function initRecipients()
    {
        ndx3 = 0;
        editForm.propRecipients.options.length = 0;
        for (ndx = 0; ndx < DBrecipients.length; ++ndx)
        {
            if (DBrecipients[ndx].indexOf("@") > -1)
            {
                editForm.propRecipients.options[ndx3] = new Option(DBrecipients[ndx], DBrecipients[ndx]);
                ++ndx3;
            }
            else if (DBrecipients[ndx].charAt(0) == "/")
            {
                for (ndx2 = 0; ndx2 < DBgroupsList.length; ++ndx2)
                {
                    if (DBrecipients[ndx] == DBgroupsVals[ndx2])
                    {
                        gText = DBgroupsList[ndx2] + " Context Curators";
                        editForm.propRecipients.options[ndx3] = new Option(gText, DBgroupsVals[ndx2]);
                        ++ndx3;
                        break;
                    }
                }
            }
            else
            {
                for (ndx2 = 0; ndx2 < DBnamesVals.length; ++ndx2)
                {
                    if (DBrecipients[ndx] == DBnamesVals[ndx2])
                    {
                        editForm.propRecipients.options[ndx3] = new Option(DBnamesList[ndx2], DBnamesVals[ndx2]);
                        ++ndx3;
                        break;
                    }
                }
            }
        }
    }

    function loaded()
    {
        MtabGroup1 = [
            [tabMain1, tabProp],
            [tabMain2, tabReport],
            [tabMain3, tabQual],
            [tabMain4, tabMon]
        ];

        tabProp.style.position = "absolute";
        tabProp.style.visibility = "visible";
        tabMon.style.position = "absolute";
        tabMon.style.top = tabProp.style.top;
        tabMon.style.left = tabProp.style.left;
        tabMon.style.visibility = "hidden";
        tabReport.style.position = "absolute";
        tabReport.style.top = tabProp.style.top;
        tabReport.style.left = tabProp.style.left;
        tabReport.style.visibility = "hidden";
        tabQual.style.visibility = "hidden";

        MusersTab = propUList;
        if (editForm.usersTab.value != "0")
        {
            selectTab0(propGList);
        }
        else
        {
            setEmailList();
        }
        MmainTab = tabMain1;
        if (editForm.mainTab.value != "0")
        {
            selectTab1(MtabGroup1[editForm.mainTab.value][0]);
        }
        initContexts();
        initActWorkflow();
        initActRegStatus();
        initInfoRegStatus();
        initInfoWorkflow();
        initACTypes();
        setNameList(editForm.infoCreator);
        setNameList(editForm.infoModifier);
        initRecipients();
        loaded0();
        editForm.actWorkflowStatus.options[0] = null;
        editForm.actRegStatus.options[0] = null;

        if (editForm.actVersion[3].checked)
        {
            editForm.actVerNum.value = DBactVerNum;
            editForm.actVerNum.disabled = false;
        }
        else
        {
            editForm.actVerNum.value = "";
        }

        // Update the sample display.
        setToSample();
        nameChanged(editForm.propName.value);
        editForm.save1.disabled = false;
        editForm.save2.disabled = false;
        saveCheck();
    }

    function setActVerNum(value)
    {
        if (value == "S")
        {
            editForm.actVerNum.disabled = false;
            editForm.actVerNum.value = DBactVerNum;
        }
        else
        {
            if (editForm.actVerNum.value !== "")
            {
                DBactVerNum = editForm.actVerNum.value;
            }
            editForm.actVerNum.value = "";
            editForm.actVerNum.disabled = true;
        }
    }

    function setFreq(value)
    {
        if (value == "D")
        {
            editForm.freqWeekly.disabled = true;
            editForm.freqMonthly.disabled = true;
        }
        else if (value == "W")
        {
            editForm.freqWeekly.disabled = false;
            editForm.freqMonthly.disabled = true;
        }
        else
        {
            editForm.freqWeekly.disabled = true;
            editForm.freqMonthly.disabled = false;
        }
    }

    function nameChanged(value)
    {
/*        sampleName.innerText = (value == null || value == "") ?
            "&nbsp;" : value; */
    }

    function addFEmail()
    {
        if (editForm.propEmail.value !== null && editForm.propEmail.value !== "")
        {
            if (editForm.propEmail.value.indexOf("@") == -1)
            {
                window.alert("The email address is not correctly formed.");
                editForm.propEmail.focus();
                return;
            }
            if (findChosenText(editForm.propEmail.value) === false)
            {
                index = editForm.propRecipients.options.length;
                editForm.propRecipients.options[index] = new Option(editForm.propEmail.value, editForm.propEmail.value);
                setToSample();
            }
            editForm.propEmail.value = "";
            editForm.propEmail.focus();
        }
    }

    function setToSample()
    {
        if (editForm.propRecipients.options.length > 0)
        {
            cstr = editForm.propRecipients.options[0].text;
            for (index = 1; index < editForm.propRecipients.options.length; ++index)
            {
                cstr = cstr + ", " + editForm.propRecipients.options[index].text;
            }
        }
        else
        {
            cstr = "";
        }
        sampleAddr.innerText = cstr;
    }

    function findChosenText(value)
    {
        for (indx2 = 0; indx2 < editForm.propRecipients.options.length; ++indx2)
        {
            if (editForm.propRecipients.options[indx2].text == value)
            {
                return true;
            }
        }
        return false;
    }

    function findChosenValue(value)
    {
        for (indx2 = 0; indx2 < editForm.propRecipients.options.length; ++indx2)
        {
            if (editForm.propRecipients.options[indx2].value == value)
            {
                return editForm.propRecipients.options[indx2].text;
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
        insert = editForm.propRecipients.options.length;
        for (index = 0; index < editForm.propUsers.options.length; ++index)
        {
            if (editForm.propUsers.options[index].selected === true)
            {
                editForm.propUsers.options[index].selected = false;
                if (findChosenValue(editForm.propUsers.options[index].value) === null)
                {
                    pObj = editForm.propUsers.options[index];
                    editForm.propRecipients.options[insert] = new Option(pObj.text, pObj.value);
                    ++insert;
                }
            }
        }
        setToSample();
    }

    function removeFromEmail()
    {
        for (index = editForm.propRecipients.options.length - 1; index >= 0; --index)
        {
            if (editForm.propRecipients.options[index].selected === true)
            {
                if (editForm.creatorID.value == editForm.propRecipients.options[index].value)
                {
                    alert("The Creator can not be removed from the recipient list.");
                    editForm.propRecipients.options[index].selected = false;
                }
                else
                {
                    editForm.propRecipients.options[index] = null;
                }
            }
        }
        setToSample();
    }

    function clearSelected(selObj)
    {
        for (index = 0; index < selObj.options.length; ++index)
        {
            selObj.options[index].selected = false;
        }
    }

    function setSelected(sobj, vals)
    {
        sobj.options[0].selected = false;
        for (index = 0; index < sobj.options.length; ++index)
        {
            for (index2 = 0; index2 < vals.length; ++index2)
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
        for (index = 1; index < field.options.length; ++index)
        {
            if (field.options[index].selected === true)
            {
                selVals[selCnt++] = field.options[index].value;
            }
        }
        tobj.options.length = 0;
        index3 = 0;
        tobj.options[index3] = new Option(list[0], vals[0]);
        for (index2 = 1; index2 < cvals.length; ++index2)
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
        if (editForm.infoSchemes.options[0].selected === true)
        {
            initSchemeItems();
            editForm.infoSchemeItems.options[0].selected = true;
        }
        else
        {
            setDependant(editForm.infoSchemes, editForm.infoSchemeItems, DBschemeItemList, DBschemeItemVals, DBschemeItemSchemes);
        }
    }

    function changedContext()
    {
        if (editForm.infoContext.options[0].selected === true)
        {
            initConceptDomain();
            initSchemes();
            initProtos();
            initForms();
//            editForm.infoConceptDomain.options[0].selected = true;
            editForm.infoSchemes.options[0].selected = true;
            editForm.infoForms.options[0].selected = true;
            editForm.infoProtos.options[0].selected = true;
            changedCS();
        }
        else
        {
//            setDependant(editForm.infoContext, editForm.infoConceptDomain, DBconceptList, DBconceptVals, DBconceptContexts);
            setDependant(editForm.infoContext, editForm.infoSchemes, DBschemeList, DBschemeVals, DBschemeContexts);
            setDependant(editForm.infoContext, editForm.infoProtos, DBprotoList, DBprotoVals, DBprotoContexts);
            setDependant(editForm.infoContext, editForm.infoForms, DBformsList, DBformsVals, DBformsContexts);
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
                fixListAllSpecials(editForm.actRegStatus, 0);
            }
            else if (selObj.name == "infoWorkflow")
            {
                fixListAllSpecials(editForm.actWorkflowStatus, 0);
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
                fixListAllSpecials(editForm.infoRegStatus, 0);
            }
            else if (selObj.name == "actWorkflowStatus")
            {
                fixListAllSpecials(editForm.infoWorkflow, 0);
            }
        }
    }

    function disableInfoWorkflowStatus(flag, vis)
    {
//        editForm.infoWorkflowStatus.disabled = flag;
//        editForm.actWorkflowStatus.disabled = flag;
    }

    function disableInfoRegStatus(flag, vis)
    {
//        editForm.infoRegStatus.disabled = flag;
//        editForm.actRegStatus.disabled = flag;
    }

    function disableInfoSchemes(flag, vis)
    {
        editForm.infoSchemes.disabled = flag;
    }

    function disableInfoVDT(flag, vis)
    {/*
        editForm.infoVDTE.disabled = flag;
        editForm.infoVDTN.disabled = flag;
        editForm.actVDT[0].disabled = flag;
        editForm.actVDT[1].disabled = flag;
        editForm.actVDT[2].disabled = flag;
        editForm.actVDT[3].disabled = flag;
   */}

    function disableInfoCreator(flag, vis)
    {
        editForm.infoCreator.disabled = flag;
    }

    function disableInfoModifier(flag, vis)
    {
        editForm.infoModifier.disabled = flag;
    }

    function disableInfoVersion(flag, vis)
    {/*
        editForm.infoVersion[0].disabled = flag;
        editForm.infoVersion[1].disabled = flag;
        editForm.infoVersion[2].disabled = flag;
        if (flag)
        {
            editForm.infoVerNum.disabled = true;
        }
        else
        {
            if (editForm.infoVersion[2].checked == true)
                editForm.infoVerNum.disabled = false;
        }
*/
        editForm.actVersion[0].disabled = flag;
        editForm.actVersion[1].disabled = flag;
        editForm.actVersion[2].disabled = flag;
        editForm.actVersion[3].disabled = flag;
        if (flag)
        {
            editForm.actVerNum.disabled = true;
        }
        else
        {
            if (editForm.actVersion[3].checked === true)
            {
                editForm.actVerNum.disabled = false;
            }
        }
    }

    function disableInfoContextUse(flag, vis)
    {/*
        editForm.infoContextUse[0].disabled = flag;
        editForm.infoContextUse[1].disabled = flag;
        editForm.infoContextUse[2].disabled = flag;
        editForm.actContextUse[0].disabled = flag;
        editForm.actContextUse[1].disabled = flag;
  */}

    function disableInfoSearchIn(flag, vis)
    {
//        editForm.infoSearchIn.disabled = flag;
//        editForm.infoSearchTerm.disabled = flag;
    }

    function disableInfoConceptDomain(flag, vis)
    {
//        editForm.infoConceptDomain.disabled = flag;
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
        for (indx2 = 0; indx2 < funcTable.length; ++indx2)
        {
            bDisable = (flagVector[indx2] == 1) ? true : false;
            tVisible = (bDisable) ? "hidden" : "visible";
            funcTable[indx2](bDisable, tVisible);
        }
    }

    function changedAC2()
    {
        flagVector = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0];
        tVect = null;
        if (editForm.infoSearchFor.options[0].selected === false)
        {
            for (index = 1; index < editForm.infoSearchFor.options.length; ++index)
            {
                if (editForm.infoSearchFor.options[index].selected === true)
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
//        initSearchAttrs();
//        initDisplayAttrs();
    }
    
    var MusersTab = null;
    var MmainTab = null;
    
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
        if (obj == propUList)
        {
            setEmailList();
            editForm.usersTab.value = "0";
        }
        else
        {
            setGroupList();
            editForm.usersTab.value = "1";
        }
    }
    
    function selectTab1(obj)
    {
        MmainTab.className = "tab1";
        obj.className = "tab0";
        for (ndx = 0; ndx < MtabGroup1.length; ++ndx)
        {
            tab = MtabGroup1[ndx];
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
                editForm.mainTab.value = ndx;
                break;
            }
        }
    }

    function cmdHelp()
    {
        window.open("/cadsrsentinel/html/help.html", "_blank");
    }

    function cmdSave()
    {
        beginDate = Date.parse(editForm.propBeginDate.value);
        endDate = Date.parse(editForm.propEndDate.value);

		if (editForm.actVerNum.value.search(/[^0-9.]/g) > -1)
		{
            selectTab1(tabMain4);
            editForm.actVerNum.focus();
            alert("A Version number can only contain digits and a period.");
		}
        else if (editForm.propName.value === "")
        {
            selectTab1(tabMain1);
            editForm.propName.focus();
            alert("A Sentinel Name must be provided.");
        }
        else if (editForm.propStatus[3].checked === true && MstatusReason === false)
        {
            selectTab1(tabMain1);
            editForm.propStatusReason.value = "(please explain why this is inactive)";
            editForm.propStatusReason.focus();
            alert("A reason must be given when the Status is set to Inactive.");
        }
        else if (editForm.propStatus[2].checked === true && editForm.propBeginDate.value === "" &&
            editForm.propEndDate.value === "")
        {
            selectTab1(tabMain1);
            editForm.propBeginDate.focus();
            alert("A begin and/or end date must be given when the Status is set to Active with Dates.");
        }
        else if (editForm.propBeginDate.value !== "" && isNaN(beginDate) === true)
        {
            selectTab1(tabMain1);
            editForm.propBeginDate.focus();
            alert("The begin date is incorrect.");
        }
        else if (editForm.propEndDate.value !== "" && isNaN(endDate) === true)
        {
            selectTab1(tabMain1);
            editForm.propEndDate.focus();
            alert("The end date is incorrect.");
        }
        else if (isNaN(beginDate) === false && isNaN(endDate) === false && endDate < beginDate)
        {
            selectTab1(tabMain1);
            editForm.propBeginDate.focus();
            alert("The begin date must preceed the end date.");
        }
        else if (editForm.infoForms.options[0].selected === false &&
            (editForm.infoSchemes.options[0].selected === false || editForm.infoSchemeItems.options[0].selected === false))
        {
            selectTab1(tabMain3);
            alert("Classification Schemes/Items and Forms can not both be selected for criteria.  Please change one or both to (All).");
        }
        else // We think the form is good to go.
        {
            for (ndx = 0; ndx < editForm.propRecipients.options.length; ++ndx)
            {
                editForm.propRecipients.options[ndx].selected = true;
            }
            editForm.nextScreen.value = "save";
            editForm.save1.disabled = true;
            editForm.save2.disabled = true;
            editForm.submit();
        }
    }

    function cmdClear()
    {
        editForm.submit();
    }

    function cmdRun()
    {
		if (editForm.actVerNum.value.search(/[^0-9.]/g) > -1)
		{
            selectTab1(tabMain4);
            editForm.actVerNum.focus();
            alert("A Version number can only contain digits and a period.");
		}
		else
		{
	        for (ndx = 0; ndx < editForm.propRecipients.options.length; ++ndx)
            {
	            editForm.propRecipients.options[ndx].selected = true;
            }
	        editForm.nextScreen.value = "run";
	        editForm.submit();
	    }
    }

    function cmdBack()
    {
        editForm.nextScreen.value = "back";
        editForm.submit();
    }
    
    function cmdLogout()
    {
        editForm.action = "/cadsrsentinel/do/logout";
        editForm.submit();
    }

    function saved(val)
    {
        if (val == "Y")
        {
            alert("Successful save.");
            editForm.nextScreen.value = "list";
            editForm.submit();
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
        editForm.propStatusReason.select();
    }
    