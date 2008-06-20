/* Copyright ScenPro, Inc. 2005
   $Header: /share/content/gforge/sentinel/sentinel/WebRoot/js/list.js,v 1.3 2008-06-20 20:44:30 hebell Exp $
   $Name: not supported by cvs2svn $
*/

    var checkCount = 0;
    var lastSort = null;
    var lastSortCol = 0;
    var sortChar = "";
    var lastSortOrder = true;

    function loaded()
    {
        disableButs();
        stripeTable();
        var obj1 = theList;
        var obj2 = obj1.children[1];
        var obj3 = obj2.children[0];
        var obj4 = obj3.children[1];
        var obj5 = obj4.children[1];
        sortCol(obj5, 1);
        saved();
    }

    function cmdHelp()
    {
        window.open(helpUrl, "_blank");
    }

    function cmdCreate()
    {
        listForm.nextScreen.value = "create";
        listForm.submit();
    }

    function cmdDelete()
    {
        var delCount = 0;
        var delNames = "";
        var obj1 = theList;
        var obj2 = obj1.children[1];
        for (var indx2 = 1; indx2 < obj2.children.length; ++indx2)
        {
            var obj3 = obj2.children[indx2];
            var obj4 = obj3.children[0];
            var obj5 = obj4.children[0];
            if (obj5.checked === true)
            {
                obj5 = obj3.children[1];
                ++delCount;
                delNames = delNames + ', "' + obj5.innerText + '"';
            }
        }
        if (window.confirm("Are you sure you wish to delete the " + delCount + " sentinels " + delNames.substr(2) + "?"))
        {
            listForm.nextScreen.value = "delete";
            listForm.submit();
        }
    }

    function cmdRun()
    {
        listForm.nextScreen.value = "run";
        listForm.submit();
    }

    function cmdEdit()
    {
        listForm.nextScreen.value = "edit";
        listForm.submit();
    }

    function cmdNewFrom()
    {
        listForm.nextScreen.value = "newfrom";
        listForm.submit();
    }

    function stripeTable()
    {
        var obj1 = theList;
        var obj2 = obj1.children[1];
        var obj3 = obj2.children[0];
        obj3.style.backgroundColor = "#dddddd";
        for (var indx2 = 1; indx2 < obj2.children.length; ++indx2)
        {
            obj3 = obj2.children[indx2];
            obj3.style.backgroundColor = ((indx2 % 2) === 0) ? "#e0f0ff" : "#ffffff";
        }
    }

    function setAllChecks()
    {
        var obj1 = theList;
        var obj2 = obj1.children[1];
        var obj3 = obj2.children[0];
        var obj4 = obj3.children[0];
        var obj5 = obj4.children[0];
        var flag = obj5.checked;
        for (var indx2 = 1; indx2 < obj2.children.length; ++indx2)
        {
            obj3 = obj2.children[indx2];
            obj4 = obj3.children[0];
            obj5 = obj4.children[0];
            obj5.checked = flag;
        }
        checkCount = (flag) ? indx2 - 1 : 0;
        fixButs2();
    }

    function disableButs()
    {
        var flags = [false, true, true, false, true];
        if (checkCount === 0)
        {
            flags[3] = true;
        }
        else if (checkCount == 1)
        {
            flags = [false, false, false, false, false];
        }
        for (var index = 0; index < flags.length; ++index)
        {
            cmdButsTop.children[index].disabled = flags[index];
            cmdButsBtm.children[index].disabled = flags[index];
        }
    }

    function fixButs(butObj)
    {
        if (butObj.checked === true)
        {
            ++checkCount;
        }
        else
        {
            --checkCount;
        }
        var obj1 = theList;
        var obj2 = obj1.children[1];
        var obj3 = obj2.children[0];
        var obj4 = obj3.children[0];
        var obj5 = obj4.children[0];
        if (checkCount === 0)
        {
	        obj5.checked = false;
        }
        else if (checkCount == (obj2.children.length - 1))
        {
            obj5.checked = true;
        }
            
        fixButs2();
    }

    function fixButs2()
    {
        disableButs();

        var obj1 = theList;
        var obj2 = obj1.children[1];
        for (var indx2 = 1; indx2 < obj2.children.length; ++indx2)
        {
            var obj3 = obj2.children[indx2];
            var obj4 = obj3.children[0];
            var obj5 = obj4.children[0];
            if (obj5.checked === true &&
                obj3.children[6].innerText != Muserid && Madmin === false)
            {
                cmdButsTop.children[1].disabled = true;
                cmdButsBtm.children[1].disabled = true;
                cmdButsTop.children[3].disabled = true;
                cmdButsBtm.children[3].disabled = true;
                cmdButsTop.children[4].disabled = true;
                cmdButsBtm.children[4].disabled = true;
            }
        }
    }

    function swapCols(obja, objb)
    {
        for (var index = 0; index < obja.children.length; ++index)
        {
            var temp = obja.children[index].innerHTML;
            obja.children[index].innerHTML = objb.children[index].innerHTML;
            objb.children[index].innerHTML = temp;
        }
    }

    function sortTextA(col)
    {
        var obj1 = theList;
        var obj2 = obj1.children[1];
        var maxcnt = obj2.children.length - 1;
        var loop = true;
        while (loop)
        {
            loop = false;
            for (var indx2 = 1; indx2 < maxcnt; ++indx2)
            {
                var obj3a = obj2.children[indx2];
                var obj4a = obj3a.children[col];
                var texta = obj4a.innerText;
                var obj3b = obj2.children[indx2 + 1];
                var obj4b = obj3b.children[col];
                var textb = obj4b.innerText;

                if (texta.toUpperCase() > textb.toUpperCase())
                {
                    loop = true;
                    swapCols(obj3a, obj3b);
                }
            }
        }
    }

    function sortTextD(col)
    {
        var obj1 = theList;
        var obj2 = obj1.children[1];
        var maxcnt = obj2.children.length - 1;
        var loop = true;
        while (loop)
        {
            loop = false;
            for (var indx2 = 1; indx2 < maxcnt; ++indx2)
            {
                var obj3a = obj2.children[indx2];
                var obj4a = obj3a.children[col];
                var texta = obj4a.innerText;
                var obj3b = obj2.children[indx2 + 1];
                var obj4b = obj3b.children[col];
                var textb = obj4b.innerText;

                if (texta.toUpperCase() < textb.toUpperCase())
                {
                    loop = true;
                    swapCols(obj3a, obj3b);
                }
            }
        }
    }

    function sortText(col)
    {
        if (col == lastSortCol)
        {
            if (lastSortOrder === true)
            {
                sortTextD(col);
                sortChar = "&#217;";
                lastSortOrder = false;
            }
            else
            {
                sortTextA(col);
                sortChar = "&#218;";
                lastSortOrder = true;
            }
        }
        else
        {
            sortTextA(col);
            sortChar = "&#218;";
            lastSortOrder = true;
        }
        lastSortCol = col;
    }

    function setSortHeading(obj)
    {
        if (lastSort !== null)
        {
            lastSort.innerHTML = "";
        }
        obj.innerHTML = sortChar;
        lastSort = obj;
    }

    function sortCol(obj, col)
    {
        sortText(col);
        setSortHeading(obj);
    }

    function setCursor(obj, cursor)
    {
        obj.style.cursor = cursor;
    }

    function cmdLogout()
    {
        listForm.action = "/cadsrsentinel/do/logout";
        listForm.submit();
    }
