// Copyright (c) 2006 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/audits/AuditConceptToEVS.java,v 1.1 2006-09-08 22:32:55 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.audits;

import gov.nih.nci.cadsr.sentinel.database.DBProperty;
import gov.nih.nci.cadsr.sentinel.tool.ConceptItem;
import gov.nih.nci.evs.domain.Definition;
import gov.nih.nci.evs.domain.Property;
import gov.nih.nci.evs.domain.DescLogicConcept;
import gov.nih.nci.evs.domain.MetaThesaurusConcept;
import gov.nih.nci.evs.domain.Source;
import gov.nih.nci.evs.query.EVSQuery;
import gov.nih.nci.evs.query.EVSQueryImpl;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.ApplicationService;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 * This class compares the caDSR Concepts table to the referenced EVS Concepts. If the concept code or name is not
 * valid an appropriate message is returned. Concepts which match EVS are not reported.
 * 
 * @author lhebel
 *
 */
public class AuditConceptToEVS extends AuditReport
{

    /* (non-Javadoc)
     * @see gov.nih.nci.cadsr.sentinel.tool.AuditReport#getTitle()
     */
    @Override
    public String getTitle()
    {
        return "caDSR / EVS Concept Inconsistencies";
    }

    /* (non-Javadoc)
     * @see gov.nih.nci.cadsr.sentinel.tool.AuditReport#getReportRows()
     */
    @Override
    public String[] getReportRows()
    {
        Vector<String> msgs = validate();
        String[] rows = new String[msgs.size()];
        for (int i = 0; i < rows.length; ++i)
        {
            rows[i] = msgs.get(i);
        }
        return rows;
    }

    /* (non-Javadoc)
     * @see gov.nih.nci.cadsr.sentinel.tool.AuditReport#okToDisplayCount()
     */
    @Override
    public boolean okToDisplayCount()
    {
        return true;
    }

    /* (non-Javadoc)
     * @see gov.nih.nci.cadsr.sentinel.tool.AuditReport#rightJustifyLastColumn()
     */
    @Override
    public boolean rightJustifyLastColumn()
    {
        return false;
    }

    /**
     * Parse the DBProperties and create appropriate EVSVocab objects.
     * 
     * @param props_ the property (key/value pairs)
     * @return the equivalent vocab objects
     */
    private EVSVocab[] parseProperties(DBProperty[] props_)
    {
        Vector<EVSVocab> vocabs = new Vector<EVSVocab>();
        String defProp = "";

        // Get defaults
        for (int i = 0; i < props_.length; ++i)
        {
            String[] text = props_[i]._key.split("[.]");
            if (text[2].equals("ALL"))
            {
                if (text[3].equals("PROPERTY") && text[4].equals("DEFINITION"))
                    defProp = props_[i]._value;
                break;
            }
        }

        // Process vocab data
        EVSVocab vocab = null;
        
        vocab = new EVSVocab();
        vocab._display = "MetaThesaurus";
        vocab._vocab = "MetaThesaurus";
        vocab._source = "NCI_META_CUI";
        vocab._source2 = "UMLS_CUI";
        vocab._ed = new MetaTh(vocab);
        vocabs.add(vocab);

        String vDisplay = null;
        String vName = null;
        String vDefProp = null;
        String vSearch = null;
        String vSource = null;
        String last = null;
        for (int i = 0; i < props_.length; ++i)
        {
            String[] text = props_[i]._key.split("[.]");
            if (!text[2].equals("ALL"))
            {
                if (last == null)
                    last = text[2];

                if (!last.equals(text[2]))
                {
                    vocab = new EVSVocab();
                    vocab._display = vDisplay;
                    vocab._vocab = vName;
                    vocab._preferredDefinitionProp = (vDefProp == null) ? defProp : vDefProp;
                    vocab._preferredNameProp = vSearch;
                    vocab._ed = new NonMetaTh(vocab);
                    vocab._source = vSource;
                    vocabs.add(vocab);
                    vDisplay = null;
                    vName = null;
                    vDefProp = null;
                    vSearch = null;
                    vSource = null;
                    last = text[2];
                }

                if (text.length == 4)
                {
                    if (text[3].equals("DISPLAY"))
                        vDisplay = props_[i]._value;
                    else if (text[3].equals("EVSNAME"))
                    {
                        vName = props_[i]._value;

                        if (vName.equals("NCI_Thesaurus"))
                            vSource = "NCI_CONCEPT_CODE";
                        else if (vName.equals("LOINC"))
                            vSource = "LOINC_CODE";
                        else if (vName.equals("GO"))
                            vSource = "GO_CODE";
                        else if (vName.equals("MedDRA"))
                            vSource = "MEDDRA_CODE";
                        else if (vName.equals("SNOMED_CT"))
                            vSource = "SNOMED_CODE";
                        else if (vName.equals("VA_NDFRT"))
                            vSource = "VA_NDF_CODE";
                        else if (vName.equals("MGED_Ontology"))
                            vSource = "NCI_MO_CODE";
                    }
                }
                else if (text.length == 5)
                {
                    if (text[3].equals("PROPERTY"))
                    {
                        if (text[4].equals("DEFINITION"))
                        {
                            vDefProp = props_[i]._value;
                        }
                        else if (text[4].equals("NAMESEARCH"))
                        {
                            vSearch = props_[i]._value;
                        }
                    }
                }
            }
        }

        vocab = new EVSVocab();
        vocab._display = vDisplay;
        vocab._vocab = vName;
        vocab._preferredDefinitionProp = (vDefProp == null) ? defProp : vDefProp;
        vocab._preferredNameProp = vSearch;
        vocab._ed = new NonMetaTh(vocab);
        vocab._source = vSource;
        vocabs.add(vocab);

        EVSVocab[] rs = new EVSVocab[vocabs.size()];
        for (int i = 0; i < rs.length; ++i)
        {
            rs[i] = vocabs.get(i);
        }
        return rs;
    }
    
    private class EVSVocab
    {
        /**
         * Constructor
         */
        public EVSVocab()
        {
        }
        
        /**
         */
        public String _vocab;
        
        /**
         */
        public String _display;

        /**
         */
        public String _preferredNameProp;

        /**
         */
        public String _preferredDefinitionProp;

        /**
         */
        public String _source;

        /**
         */
        public String _source2;
        
        /**
         */
        public EVSData _ed;
    }
    
    private abstract class EVSData
    {
        /**
         * Constructor
         * 
         * @param vocab_ the vocabulary description 
         */
        public EVSData(EVSVocab vocab_)
        {
            reset();
            _vocab = vocab_;
        }
        
        /**
         * Reset the data elements to empty.
         *
         */
        public void reset()
        {
            _msg = "";
            _flag = true;
        }

        /**
         * Determine the recommended concept name when it is missing.
         * 
         * @return the recommended concept name
         */
        abstract public String recommendName();

        /**
         * Search EVS for the Concept Code.
         * 
         * @param query_ the EVSQuery defined by the caCORE API
         */
        abstract public void search(EVSQuery query_);

        /**
         * Validate the Concept Code, Concept Name and Concept Definition.
         */
        abstract public void validate();

        /**
         * The messages from the validate() method.
         */
        public String _msg;

        /**
         * The recommended name for a Concept.
         */
        public String _name;

        /**
         * The caDSR Concept record to validate.
         */
        public ConceptItem _rec;

        /**
         * The validation flag, true indicates the Name does not match.
         */
        public boolean _flag;

        /**
         * The concept list returned by the caCORE API EVS Query.
         */
        public List _cons;

        /**
         * The name property list use to validate the concept name.
         */
        public EVSVocab _vocab;
    }
    
    private class MetaTh extends EVSData
    {
        /**
         * Constructor
         * 
         * @param vocab_ the EVS vocab description 
         */
        public MetaTh(EVSVocab vocab_)
        {
            super(vocab_);
        }

        @Override
        public void search(EVSQuery query_)
        {
            query_.searchMetaThesaurus(_rec._preferredName);
        }

        @Override
        public String recommendName()
        {
            MetaThesaurusConcept obj = (MetaThesaurusConcept) _cons.get(0);
            return obj.getName();
        }

        @Override
        public void validate()
        {
            try
            {
                // The search returns a list of results so process all.
                for (int i = 0; i < _cons.size(); ++i)
                {
                    // The objects are Meta Thesaurus
                    MetaThesaurusConcept temp = (MetaThesaurusConcept) _cons.get(i);
                    
                    // Check the default name.
                    if (_rec._longName.compareToIgnoreCase(temp.getName()) == 0)
                    {
                        _flag = false;
                        break;
                    }
                    
                    // Check the synonyms if the default name didn't match.
                    ArrayList value = temp.getSynonymCollection();
                    if (value.indexOf(_rec._longName) > -1)
                    {
                        _flag = false;
                        break;
                    }
                }
                // We didn't find a name so recommend one.
                if (_flag)
                    _name = recommendName();
            }
            catch (ClassCastException ex)
            {
                // The data in the caDSR is mislabeled. This concept code is not for Meta.
                _msg += "\nMislabeled as a MetaThesaurus Concept.";
                _flag = false;
                _logger.warn(ex.toString());
                
                // Can't check anything else when this exception happens.
                return;
            }

            // Must have a definition source to proceed.
            if (_rec._definitionSource == null || _rec._definitionSource.length() == 0)
            {
                boolean defFlag = false;
                for (int i = 0; i < _cons.size(); ++i)
                {
                    // Need definitions.
                    MetaThesaurusConcept temp = (MetaThesaurusConcept) _cons.get(i);
                    @SuppressWarnings("unchecked")
                    ArrayList<Definition> defs = temp.getDefinitionCollection();
                    if (defs != null && defs.size() > 0)
                    {
                        defFlag = true;
                        break;
                    }
                }
                if (defFlag)
                    _msg += "\nThe definition source is missing and EVS has possible definitions.";
                else if (_rec._preferredDefinition.length() > 0)
                    _msg += "\nEVS has no definitions for this term and the caDSR contains definition text.";
            }
            else
            {
                // Process the search results again only this time for the definition.
                boolean srcFlag = true;
                boolean defFlag = true;
                boolean defCol = true;
                String defSource = null;
                int full = 0;
                for (int i = 0; i < _cons.size() && full < 3 && srcFlag && defFlag; ++i)
                {
                    // Need definitions.
                    MetaThesaurusConcept temp = (MetaThesaurusConcept) _cons.get(i);
                    @SuppressWarnings("unchecked")
                    ArrayList<Definition> defs = temp.getDefinitionCollection();
                    if (defs != null && defs.size() > 0)
                    {
                        defCol = false;

                        // Check the definition source and definition text.
                        for (Definition def : defs)
                        {
                            full = 0;
                            Source defsor = def.getSource();
                            if (defsor != null && defsor.getAbbreviation().equals(_rec._definitionSource))
                            {
                                srcFlag = false;
                                full += 1;
                            }
                            if (def.getDefinition().equals(_rec._preferredDefinition))
                            {
                                if (defsor != null)
                                    defSource = defsor.getAbbreviation();
                                defFlag = false;
                                full += 2;
                            }
                            if (full == 3)
                                break;
                        }
                    }
                }
                
                // Did we find everything?
                if (full == 3)
                    return;

                if (defCol)
                {
                    _msg += "\nNo definitions exist in EVS and the caDSR contains a definition source [" + _rec._definitionSource + "]";
                    return;
                }

                if (srcFlag)
                {
                    if (defFlag)
                        _msg += "\nDefinition Source [" + _rec._definitionSource + "] does not exist for this Concept.";
                    else if (defSource != null)
                        _msg += "\nDefinition matches source [" + defSource + "] but was expecting source to be [" + _rec._definitionSource + "]";
                    else
                        _msg += "\nDefinition matches unnamed source but was expecting source to be [" + _rec._definitionSource + "]";
                }

                if (defFlag)
                    _msg += "\nDefinition does not match EVS. [" + _rec._definitionSource + "]";
            }
        }
    }
    
    private class NonMetaTh extends EVSData
    {
        /**
         * Constructor
         * 
         * @param vocab_ the EVS vocab description 
         */
        public NonMetaTh(EVSVocab vocab_)
        {
            super(vocab_);
        }

        @Override
        public void search(EVSQuery query_)
        {
            query_.getDescLogicConcept(_vocab._vocab, _rec._preferredName, true);
        }

        @Override
        public String recommendName()
        {
            DescLogicConcept obj = (DescLogicConcept) _cons.get(0);

            if (_vocab._preferredNameProp == null)
                return obj.getName();
            
            String name = "no recommendations available";
            
            @SuppressWarnings ("unchecked")
            Vector<Property> collection = obj.getPropertyCollection();
            if (collection == null || collection.size() == 0)
            {
                String temp = obj.getName();
                if (temp != null && temp.length() > 0)
                    name = temp;
            }
            else
            {
                for (Property prop : collection)
                {
                    if (prop.getName().equals(_vocab._preferredNameProp))
                    {
                        name = prop.getValue();
                        break;
                    }
                }
            }
            
            return name;
        }

        @Override
        public void validate()
        {
            try
            {
                for (int i = 0; i < _cons.size(); ++i)
                {
                    Vector collection = null;
                    Property prop = null;
                    DescLogicConcept temp = (DescLogicConcept) _cons.get(i);
                    if (_vocab._preferredNameProp == null)
                    {
                        if (temp.getName().compareToIgnoreCase(_rec._longName) == 0)
                        {
                            _flag = false;
                            break;
                        }
                        collection = temp.getPropertyCollection();
                    }
                    else
                    {
                        collection = temp.getPropertyCollection();
    
                        // Searching a Vector class is best using the Vector.contains() method. Depending
                        // on the vocabulary different attribute names must be used to match the name. This
                        // information is also stored in the tool_options table for the Curation Tool.
                        prop = new Property();
                        prop.setName(_vocab._preferredNameProp);
                        prop.setValue(_rec._longName);
                        if (collection.contains(prop))
                        {
                            _flag = false;
                            break;
                        }
                    }
                        
                    // In the off chance none of the current defined attributes contain the name, perhaps
                    //  an attribute was removed from the Curation Tool options after a concept was
                    // copied from EVS.
                    for (int n = 0; n < collection.size(); ++n)
                    {
                        prop = (Property) collection.get(n);
                        if (_rec._longName.compareToIgnoreCase(prop.getValue()) == 0)
                        {
                            _msg += "Name matches on property " + prop.getName() + " but expected to match on property " + ((_vocab._preferredNameProp == null) ? "(default)" : _vocab._preferredNameProp);
                            _flag = false;
                            break;
                        }
                    }
                }
                if (_flag)
                    _name = recommendName();
            }
            catch (ClassCastException ex)
            {
                // The Concept is marked as a non-Meta source and it should be Meta.
                _msg += "\nMislabeled, should be a MetaThesaurus Concept.";
                _flag = false;
                _logger.warn(ex.toString());
                
                // Can't continue if this exception occurs.
                return;
            }

            boolean srcFlag = true;
            boolean defFlag = true;
            for (int i = 0; i < _cons.size() && srcFlag && defFlag; ++i)
            {
                DescLogicConcept temp = (DescLogicConcept) _cons.get(i);
                @SuppressWarnings("unchecked")
                Vector<Property> props = temp.getPropertyCollection();
                for (Property prop : props)
                {
                    if (prop.getName().equals(_vocab._preferredDefinitionProp))
                    {
                        srcFlag = false;
                        int pDefSrc;
                        int tDefSrc;
                        
                        pDefSrc = prop.getValue().indexOf("<def-source>");
                        if (pDefSrc >= 0)
                        {
                            pDefSrc += "<def-source>".length();
                            String text = prop.getValue().substring(pDefSrc);
                            tDefSrc = text.indexOf("</def-source>");
                            text = text.substring(0, tDefSrc);
                            if (text.length() == 0)
                            {
                                if  (_rec._definitionSource != null && _rec._definitionSource.length() > 0)
                                    _msg += "\nThe EVS definition source is missing, caDSR is [" + _rec._definitionSource + "].";
                            }
                            else
                            {
                                if  (_rec._definitionSource == null || _rec._definitionSource.length() == 0)
                                    _msg += "\nThe caDSR definition source is missing, EVS is [" + text + "].";
                                else
                                {
                                    if (!text.equals(_rec._definitionSource))
                                        _msg += "\nThe caDSR definition source does not match EVS. [" + _rec._definitionSource + "] [" + text + "]";
                                }
                            }
                        }
                        
                        pDefSrc = prop.getValue().indexOf("<def-definition>");
                        if (pDefSrc < 0)
                        {
                            if (prop.getValue().equals(_rec._preferredDefinition))
                            {
                                defFlag = false;
                            }
                        }
                        else
                        {
                            pDefSrc += "<def-definition>".length();
                            String text = prop.getValue().substring(pDefSrc);
                            tDefSrc = text.indexOf("</def-definition>");
                            if (text.substring(0, tDefSrc).equals(_rec._preferredDefinition))
                            {
                                defFlag = false;
                            }
                        }
                        break;
                    }
                }
            }
            if (srcFlag)
            {
                if (_rec._preferredDefinition.length() > 0)
                    _msg += "\nNo definitions exist in EVS for property [" + _vocab._preferredDefinitionProp + "] can not compare definitions.";
            }
            else if (defFlag)
            {
                if (_rec._definitionSource == null)
                    _msg += "\nDefinition does not match EVS.";
                else
                    _msg += "\nDefinition does not match EVS [" + _rec._definitionSource + "].";
            }
        }
    }
    
    /**
     * Validate the caDSR Concepts against EVS
     * 
     * @return exception, error and information messages
     */
    private Vector<String> validate()
    {
        // Seed message list with column headings.
        Vector<String> msgs = new Vector<String>();
        msgs.add(formatTitleMsg());

        // Get all the Concepts from the caDSR.
        Vector<ConceptItem> concepts = _db.selectConcepts();
        EVSVocab[] vocabs = parseProperties(_db.selectEVSVocabs());

        // Get the EVS URL and establish the application service.
        String evsURL = _db.selectEvsUrl();
        ApplicationService coreapi = ApplicationService.getRemoteInstance(evsURL);

        // Check each concept with EVS.
        String msg = null;
        String name = null;
        int count = 0;
        for (ConceptItem rec : concepts)
        {
            // Reset loop variables.
            msg = "";
            if (rec._preferredDefinition.toLowerCase().startsWith("no value exists"))
                rec._preferredDefinition = "";

            // Show status messages when debugging.
            if ((count % 100) == 0)
            {
                _logger.debug("Completed " + count + " out of " + concepts.size() + " (" + (count * 100 / concepts.size()) + "%) . Message/Failure count " + msgs.size() + " (" + String.valueOf(msgs.size() * 100 / concepts.size()) + "%)");
            }
            ++count;

            EVSVocab vocab = null;;
            while (true)
            {
                // Verify the EVS Vocabulary reference.
                if (rec._evsSource == null || rec._evsSource.length() == 0)
                {
                    msg += "\nMissing EVS Source";
                    break;
                }

                // Determine the desired vocabulary using the EVS source value in caDSR.
                // This translation should be in the tool options table or the data content of the EVS
                // source column should use the standard vocabulary abbreviation.
                for (int i = 0; i < vocabs.length; ++i)
                {
                    if (rec._evsSource.equals(vocabs[i]._source))
                    {
                        vocab = vocabs[i];
                        break;
                    }
                }
                if (vocab == null)
                {
                    if (rec._evsSource.equals(vocabs[0]._source2))
                        vocab = vocabs[0];
                }

                // We have to do either Meta or a Vocabulary.
                if (vocab == null)
                {
                    msg += "\nUnknown EVS Source " + rec._evsSource;
                    break;
                }
                
                // The concept code is required and is stored in the preferred name column - yeah don't ask.
                if (rec._preferredName== null || rec._preferredName.length() == 0)
                {
                    msg += "\nMissing Concept Code";
                    break;
                }

                EVSData ed = vocab._ed;
                ed.reset();
                ed._rec = rec;

                EVSQuery query = new EVSQueryImpl();
                ed.search(query);
                
                try
                {
                    // Get the attributes for the concept code.
                    ed._cons = coreapi.evsSearch(query);
                }
                catch (ApplicationException ex)
                {
                    // The concept code recorded in the preferred name is no longer valid.
                    if (ex.toString().indexOf("Invalid concept code") > -1)
                    {
                        msg += "\nInvalid concept code.";
                        _logger.warn(ex.toString());
                        break;
                    }
                    else if (ex.toString().indexOf("Invalid conceptID") > -1)
                    {
                        msg += "\nInvalid concept ID.";
                        _logger.warn(ex.toString());
                        break;
                    }
                    
                    // An unexpected exception occurred so record it and terminate the validation.
                    else
                    {
                        msg += "\n" + ex.toString();
                        msg = formatMsg(rec, vocab, msg);
                        msgs.add(msg);
                        _logger.error(ex.toString());
                        return msgs;
                    }
                }

                // The concept code doesn't exist in EVS.
                if (ed._cons.size() == 0)
                {
                    msg += "\nFailed to retrieve EVS concept";
                    break;
                }
                    
                // Verify the caDSR Concept has a name. This should never happen but we must
                // allow for any state in the data. This is not only because of the tiers, DEV, QA, Stage
                // and Production, but also because data corruption could occur and must be caught.
                if (rec._longName == null || rec._longName.length() == 0)
                {
                    msg += "\nMissing Concept Long Name, recommend using [" + ed.recommendName() + "]";
                    break;
                }

                // Assume we will not match the concept name.
                boolean flag = true;
                name = null;

                // Validate data.
                ed.validate();
                flag = ed._flag;
                name = ed._name;
                msg += ed._msg;
                
                // The name of the concept in the caDSR doesn't match anything in EVS for this concept code.
                if (flag)
                {
                    msg += "\nConcept name does not match EVS";
                    if (name != null)
                        msg += ", expected [" + name + "]";
                }

                break;
            }

            // If something happened during the validation, record the message and continue with the next concept.
            if (msg.length() > 0)
            {
                msg = formatMsg(rec, vocab, msg);
                msgs.add(msg);
                if (msgs.size() >= _maxMsgs)
                {
                    msgs.add(formatMaxMsg());
                    break;
                }
            }
        }

        // Return all the messages, the validation processing is complete.
        return msgs;
    }
    
    private String formatMsg(ConceptItem rec_, EVSVocab vocab_, String msg_)
    {
        return rec_._longName + "::" + rec_._publicID + "::" + rec_._version + "::" + ((vocab_ == null) ? "" : vocab_._display) + "::" + rec_._preferredName + "::"
            + ((msg_.charAt(0) == '\n') ? msg_.substring(1) : msg_);
    }
    
    private String formatMaxMsg()
    {
        return "*** Maximum Messages ***::***::***::***::***::The Error Message maximum limit [" + _maxMsgs + "] has been reached, report truncated.";
    }
    
    private String formatTitleMsg()
    {
        return "Concept::Public ID::Version::Vocabulary::Concept Code::Message";
    }
    
    private static final int _maxMsgs = 200;
    private static final Logger _logger = Logger.getLogger(AuditConceptToEVS.class.getName());
}
