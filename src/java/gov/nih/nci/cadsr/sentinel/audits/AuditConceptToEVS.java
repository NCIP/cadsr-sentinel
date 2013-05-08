// Copyright (c) 2006 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/audits/AuditConceptToEVS.java,v 1.11 2008-05-15 17:35:48 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.audits;

import gov.nih.nci.cadsr.sentinel.database.DBProperty;
import gov.nih.nci.cadsr.sentinel.tool.ConceptItem;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.client.ApplicationServiceProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.LexGrid.LexBIG.DataModel.Collections.LocalNameList;
import org.LexGrid.LexBIG.DataModel.Collections.SortOptionList;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Exceptions.LBInvocationException;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.LexGrid.LexBIG.Utility.LBConstants.MatchAlgorithms;
import org.LexGrid.commonTypes.Property;
import org.LexGrid.concepts.Definition;
import org.LexGrid.concepts.Entity;
import org.LexGrid.concepts.Presentation;
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
        String vAccess = null;
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
                    vocab._access = vAccess;
                    vocab._preferredDefinitionProp = (vDefProp == null) ? defProp : vDefProp;
                    vocab._preferredNameProp = (vSearch == null) ? vDisplay : vSearch;
                    vocab._ed = new NonMetaTh(vocab);
                    vocab._source = vSource;
                    vocabs.add(vocab);
                    vDisplay = null;
                    vName = null;
                    vAccess = null;
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
                        vName = props_[i]._value;
                    else if (text[3].equals("ACCESSREQUIRED"))
                        vAccess = props_[i]._value;
                    else if (text[3].equals("VOCABCODETYPE"))
                        vSource = props_[i]._value;
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
        vocab._access = vAccess;
        vocab._preferredDefinitionProp = (vDefProp == null) ? defProp : vDefProp;
        vocab._preferredNameProp = (vSearch == null) ? vDisplay : vSearch;
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
        
        /**
         * 
         */
        public String _access;
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
        abstract public CodedNodeSet search(LexBIGService service_);

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
        public CodedNodeSet search(LexBIGService service_)
        {
        	CodedNodeSet cns = null;
        	try {
				cns = service_.getNodeSet("NCI Metathesaurus", null, null);
				cns = cns.restrictToMatchingProperties(
								Constructors.createLocalNameList("conceptCode"), 
								null, 
								_rec._preferredName, 
								MatchAlgorithms.exactMatch.name(), 
								null
							);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return cns;
        }

        @Override
        public String recommendName()
        {
        	EVSConcept obj = (EVSConcept) _cons.get(0);
        	if (obj != null) {
        		return obj.preferredName;
        	}
            return "";
        }

        @Override
        public void validate()
        {
        	// The search returns a list of results so process all.
            for (int i = 0; i < _cons.size(); ++i)
            {
                // The objects are Meta Thesaurus
            	EVSConcept temp = (EVSConcept) _cons.get(i);
                
                // Check the default name.
                if (_rec._longName.compareToIgnoreCase(temp.preferredName) == 0)
                {
                    _flag = false;
                    break;
                }
                
                // Check the synonyms if the default name didn't match.
                List value = temp.synonyms;
                if (value.indexOf(_rec._longName) > -1)
                {
                    _flag = false;
                    break;
                }
            }
            // We didn't find a name so recommend one.
            if (_flag)
                _name = recommendName();

            // Must have a definition source to proceed.
            if (_rec._definitionSource == null || _rec._definitionSource.length() == 0)
            {
                boolean defFlag = false;
                for (int i = 0; i < _cons.size(); ++i)
                {
                    // Need definitions.
                    EVSConcept temp = (EVSConcept) _cons.get(i);
                    Definition[] defs = temp.definitions;
                    if (defs != null && defs.length > 0)
                    {
                        defFlag = true;
                        break;
                    }
                }
                // The caDSR definition source is missing and EVS has possible definitions
                if (defFlag)
                    _msg += formatMsg(_MSG002);
                
                // EVS has no definitions for this term and the caDSR contains definition text
                else if (_rec._preferredDefinition.length() > 0)
                    _msg += formatMsg(_MSG003);
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
                    EVSConcept temp = (EVSConcept) _cons.get(i);
                    Definition[] defs = temp.definitions;
                    if (defs != null && defs.length > 0)
                    {
                        defCol = false;

                        // Check the definition source and definition text.
                        for (Definition def : defs)
                        {
                            full = 0;
                            org.LexGrid.commonTypes.Source[] defsors = def.getSource();
                            for (org.LexGrid.commonTypes.Source defsor: defsors) {
                            	if (defsor != null && defsor.getContent().equals(_rec._definitionSource))
                                {
                                    srcFlag = false;
                                    full += 1;
                                }
                            	
                            	if (def.getValue().getContent().equals(_rec._preferredDefinition))
                                {
                                    if (defsor != null)
                                        defSource = defsor.getContent();
                                    defFlag = false;
                                    full += 2;
                                }
                                if (full == 3)
                                    break;
                            }
                        }
                    }
                }
                
                // Did we find everything?
                if (full == 3)
                    return;

                if (defCol)
                {
                    // No definitions exist in EVS and the caDSR contains a definition source [{0}]
                    _msg += formatMsg(_MSG004, _rec._definitionSource);
                    return;
                }

                if (srcFlag)
                {
                    // Definition Source [{0}] does not exist for this Concept
                    if (defFlag)
                        _msg += formatMsg(_MSG005, _rec._definitionSource);
                    
                    // Definition matches source [{0}] but was expecting source to be [{1}]
                    else if (defSource != null)
                        _msg += formatMsg(_MSG006, defSource, _rec._definitionSource);
                    
                    // Definition matches unnamed source but was expecting source to be [{0}]
                    else
                        _msg += formatMsg(_MSG007, _rec._definitionSource);
                }

                // Definition does not match EVS. [{0}]
                else if (defFlag)
                    _msg += formatMsg(_MSG008, _rec._definitionSource);
                
                // Definition and Source found for concept but Definition matches source [{0}] and expecting source [{1}]
                else
                    _msg += formatMsg(_MSG009, defSource, _rec._definitionSource);
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
        public CodedNodeSet search(LexBIGService service_)
        {
        	CodedNodeSet cns = null;
            try {
				cns = service_.getNodeSet(_vocab._vocab, null, null);
				cns = cns.restrictToMatchingProperties(
								Constructors.createLocalNameList("conceptCode"), 
								null, 
								_rec._preferredName, 
								MatchAlgorithms.exactMatch.name(), 
								null
							);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return cns;
        }

        @Override
        public String recommendName()
        {
            EVSConcept obj = (EVSConcept) _cons.get(0);

            if (obj.preferredName != null)
                return obj.preferredName;
            
            return "no recommendations available";
        }

        @Override
        public void validate()
        {
            try
            {
                for (int i = 0; i < _cons.size(); ++i)
                {
                    List collection = null;
                    EVSConcept temp = (EVSConcept) _cons.get(i);
                    if (temp.preferredName.compareToIgnoreCase(_rec._longName) == 0)
                    {
                        _flag = false;
                        break;
                    }
                        
                    // In the off chance none of the current defined attributes contain the name, perhaps
                    //  an attribute was removed from the Curation Tool options after a concept was
                    // copied from EVS.
                    collection = temp.properties;
                    for (int n = 0; n < collection.size(); ++n)
                    {
                        org.LexGrid.commonTypes.Property prop = (org.LexGrid.commonTypes.Property) collection.get(n);
                        if (_rec._longName.compareToIgnoreCase(prop.getValue().getContent()) == 0)
                        {
                            // The collection.contains() test above doesn't always catch matching property names because of case.
                            if (preferredName.equals(prop.getPropertyName()) || _vocab._preferredNameProp.compareToIgnoreCase(prop.getPropertyName()) == 0)
                            {
                                _flag = false;
                                break;
                            }

                            // Name matches on property {0} but expected to match on property {1}
                            _msg += formatMsg(_MSG010, prop.getPropertyName(),  ((_vocab._preferredNameProp == null) ? "(default)" : _vocab._preferredNameProp));
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
                // Mislabeled, should be a MetaThesaurus Concept
                _msg += formatMsg(_MSG011);
                _flag = false;
                _logger.warn(ex.toString());
                
                // Can't continue if this exception occurs.
                return;
            }

            boolean srcFlag = true;
            boolean defFlag = true;
            for (int i = 0; i < _cons.size() && srcFlag && defFlag; ++i)
            {
            	EVSConcept temp = (EVSConcept) _cons.get(i);
                Definition[] definitions = temp.definitions;
                for (Definition def : definitions)
                {
                	if (def.isIsPreferred()) {
                		srcFlag = false;
                		org.LexGrid.commonTypes.Source[] sources = def.getSource();
                		if (sources == null || sources.length == 0) {
                			if  (_rec._definitionSource != null && _rec._definitionSource.length() > 0)
                                _msg += formatMsg(_MSG012, _rec._definitionSource);
                		}
                		else {
                			if  (_rec._definitionSource == null || _rec._definitionSource.length() == 0)
                                _msg += formatMsg(_MSG013, sources[0].getContent());
                            else
                            {
                            	boolean srcExists = false;
                            	for (org.LexGrid.commonTypes.Source src: sources) {
                            		if (src.getContent().equals(_rec._definitionSource)) {
                            			srcExists = true;
                            			break;
                            		}
                            	}
                            	
                            	if (!srcExists) {
                            		_msg += formatMsg(_MSG014, _rec._definitionSource, sources[0].getContent());
                            	}   
                            }
                		}
                		
                		if (def.getValue().getContent().equalsIgnoreCase(_rec._preferredDefinition)) {
                			defFlag = false;
                		}
                	}
                }
            }
            if (srcFlag)
            {
                // No definitions exist in EVS for property [{0}] can not compare definitions
                if (_rec._preferredDefinition.length() > 0)
                    _msg += formatMsg(_MSG015, _vocab._preferredDefinitionProp);
            }
            else if (defFlag)
            {
                // Definition does not match EVS
                if (_rec._definitionSource == null)
                    _msg += formatMsg(_MSG016);
                
                // Definition does not match EVS [{0}]
                else
                    _msg += formatMsg(_MSG017, _rec._definitionSource);
            }
        }
    }
    
    private class EVSConcept  {
    	  public String preferredName;
    	  public String code;
    	  public List synonyms;
    	  public List<org.LexGrid.commonTypes.Property> properties;
    	  public Definition[] definitions;
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
        
        LexBIGService service;
        try
        {
        	service = (LexBIGService)ApplicationServiceProvider.getApplicationService("EvsServiceInfo");
        }
        catch (Exception ex)
        {
            msgs.add("EVS API URL " + evsURL + " " + ex.toString());
            StackTraceElement[] list = ex.getStackTrace();
            for (int i = 0; i < list.length; ++i)
                msgs.add(list[i].toString());
            return msgs;
        }

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

            EVSVocab vocab = null;
            while (true)
            {
                // Missing EVS Source
                if (rec._evsSource == null || rec._evsSource.length() == 0)
                {
                    msg += formatMsg(_MSG020);
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

                // Unknown EVS Source {0}
                if (vocab == null)
                {
                    msg += formatMsg(_MSG021, rec._evsSource);
                    break;
                }
                
                // Missing Concept Code
                if (rec._preferredName== null || rec._preferredName.length() == 0)
                {
                    msg += formatMsg(_MSG022);
                    break;
                }

                EVSData ed = vocab._ed;
                ed.reset();
                ed._rec = rec;

                CodedNodeSet cns = ed.search(service);
                
                try
                {
                    // Get the attributes for the concept code. 
                    ed._cons = resolveNodeSet(cns, true);
                }
                catch (ApplicationException ex)
                {
                    // Invalid concept code
                    if (ex.toString().indexOf("Invalid concept code") > -1)
                    {
                        msg += formatMsg(_MSG023);
                        _logger.warn(ex.toString());
                        break;
                    }
                    
                    // Invalid concept ID
                    else if (ex.toString().indexOf("Invalid conceptID") > -1)
                    {
                        msg += formatMsg(_MSG024);
                        _logger.warn(ex.toString());
                        break;
                    }
                    
                    // An unexpected exception occurred so record it and terminate the validation.
                    else
                    {
                        msg += formatMsg(ex.toString());
                        // msgs.add(msg);
                        // _logger.error(ex.toString());
                        // return msgs;
                        break;
                    }
                }
                catch (Exception ex)
                {
                    msgs.add(ex.toString());
                    StackTraceElement[] list = ex.getStackTrace();
                    for (int i = 0; i < list.length; ++i)
                        msgs.add(list[i].toString());
                    return msgs;
                }

                // Failed to retrieve EVS concept
                if (ed._cons.size() == 0)
                {
                    msg += formatMsg(_MSG025);
                    break;
                }
                    
                // Missing Concept Long Name, recommend using [{0}]
                if (rec._longName == null || rec._longName.length() == 0)
                {
                    msg += formatMsg(_MSG026, ed.recommendName());
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
                    // Concept name does not match EVS
                    if (name == null)
                        msg += formatMsg(_MSG018);
                    
                    // Concept name does not match EVS, expected [{0}]
                    else
                        msg += formatMsg(_MSG019, name);
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
    
    public List<EVSConcept> resolveNodeSet(CodedNodeSet cns, boolean includeRetiredConcepts) throws Exception {
		
		if (!includeRetiredConcepts) {
			cns.restrictToStatus(CodedNodeSet.ActiveOption.ACTIVE_ONLY, null);
		}
		CodedNodeSet.PropertyType propTypes[] = new CodedNodeSet.PropertyType[2];
		propTypes[0] = CodedNodeSet.PropertyType.PRESENTATION;
		propTypes[1] = CodedNodeSet.PropertyType.DEFINITION;
		
		SortOptionList sortCriteria = Constructors.createSortOptionList(new String[]{"matchToQuery"});
		
		ResolvedConceptReferencesIterator results = cns.resolve(sortCriteria, null,new LocalNameList(), propTypes, true);
		
		return getEVSConcepts(results);
	}
    
    private List<EVSConcept> getEVSConcepts(ResolvedConceptReferencesIterator rcRefIter) throws Exception {
    	List<EVSConcept> evsConcepts = new ArrayList<EVSConcept>();
    	if (rcRefIter != null) {
    		while (rcRefIter.hasNext()) {
    			evsConcepts.add(getEVSConcept(rcRefIter.next()));
    		}
    	}
    	return evsConcepts;
    }
    
    private EVSConcept getEVSConcept(ResolvedConceptReference rcRef) {
		EVSConcept evsConcept = new EVSConcept();
		evsConcept.code = rcRef.getCode();
		
		Entity entity = rcRef.getEntity();
		evsConcept.preferredName = rcRef.getEntityDescription().getContent();
		evsConcept.definitions = entity.getDefinition();
		setPropsAndSyns(evsConcept, entity);
		
		return evsConcept;
	}
	
	private void setPropsAndSyns(EVSConcept evsConcept, Entity entity) {
		List<Property> properties = new ArrayList<Property>();
		List<String> synonyms = new ArrayList<String>();
		
		if (entity != null) {
			org.LexGrid.commonTypes.Property[] entityProps = entity.getAllProperties();
			for (org.LexGrid.commonTypes.Property entityProp: entityProps) {
				
				if (entityProp instanceof Presentation) {
					properties.add(entityProp);
				}
				else {
					String propName = entityProp.getPropertyName();
					String propValue = entityProp.getValue().getContent();
					
					if (propName.equalsIgnoreCase("FULL_SYN") || propName.equalsIgnoreCase("Synonym")) {
						synonyms.add(propValue);
					}
				}
			}
		}
		
		evsConcept.properties = properties;
		evsConcept.synonyms = synonyms;
	}
    
    private static String formatMsg(String msg_, String ... subs_)
    {
        String text = msg_;
        for (int i = 0; i < subs_.length; ++i)
        {
            String temp = "{" + i +  "}";
            text = text.replace(temp, subs_[i]);
        }
        return "\n" + text;
    }
    
    private String formatMsg(ConceptItem rec_, EVSVocab vocab_, String msg_)
    {
        return rec_._longName + AuditReport._ColSeparator
        + rec_._publicID + AuditReport._ColSeparator 
        + rec_._version + AuditReport._ColSeparator
        + ((vocab_ == null) ? "" : vocab_._display) + AuditReport._ColSeparator 
        + rec_._preferredName + AuditReport._ColSeparator
            + ((msg_.charAt(0) == '\n') ? msg_.substring(1) : msg_);
    }
    
    private String formatMaxMsg()
    {
        return "*** Maximum Messages ***" + AuditReport._ColSeparator
        + "***" + AuditReport._ColSeparator
        + "***" + AuditReport._ColSeparator
        + "***" + AuditReport._ColSeparator
        + "***" + AuditReport._ColSeparator
        + "The Error Message maximum limit [" + _maxMsgs + "] has been reached, report truncated.";
    }
    
    private String formatTitleMsg()
    {
        return "Concept" + AuditReport._ColSeparator
        + "Public ID" + AuditReport._ColSeparator
        + "Version" + AuditReport._ColSeparator
        + "Vocabulary" + AuditReport._ColSeparator
        + "Concept Code" + AuditReport._ColSeparator
        + "Message";
    }
    
    private static final String preferredName = "Preferred_Name";
    
    private static final String _MSG001 = "Mislabeled as a MetaThesaurus Concept.";
    private static final String _MSG002 = "The caDSR definition source is missing and EVS has possible definitions.";
    private static final String _MSG003 = "EVS has no definitions for this term and the caDSR contains definition text.";
    private static final String _MSG004 = "No definitions exist in EVS and the caDSR contains a definition source [{0}].";
    private static final String _MSG005 = "Definition Source [{0}] does not exist for this Concept.";
    private static final String _MSG006 = "Definition matches source [{0}] but was expecting source to be [{1}]";
    private static final String _MSG007 = "Definition matches unnamed source but was expecting source to be [{0}]";
    private static final String _MSG008 = "Definition does not match EVS. [{0}]";
    private static final String _MSG009 = "Definition and Source found for concept but Definition matches source [{0}] and expecting source [{1}]";
    private static final String _MSG010 = "Name matches on property [{0}] but expected to match on property [{1}]";
    private static final String _MSG011 = "Mislabeled, should be a MetaThesaurus Concept.";
    private static final String _MSG012 = "The EVS definition source is missing, caDSR is [{0}].";
    private static final String _MSG013 = "The caDSR definition source is missing, EVS is [{0}].";
    private static final String _MSG014 = "The caDSR definition source [{0}] does not match EVS [{1}]";
    private static final String _MSG015 = "No definitions exist in EVS for property [{0}] can not compare definitions.";
    private static final String _MSG016 = "Definition does not match EVS.";
    private static final String _MSG017 = "Definition does not match EVS [{0}].";
    private static final String _MSG018 = "Concept name does not match EVS";
    private static final String _MSG019 = "Concept name does not match EVS, expected [{0}].";
    private static final String _MSG020 = "Missing EVS Source";
    private static final String _MSG021 = "Unknown EVS Source [{0}]";
    private static final String _MSG022 = "Missing Concept Code";
    private static final String _MSG023 = "Invalid concept code.";
    private static final String _MSG024 = "Invalid concept ID.";
    private static final String _MSG025 = "Failed to retrieve EVS concept";
    private static final String _MSG026 = "Missing Concept Long Name, recommend using [{0}]";
    
    private static final int _maxMsgs = 200;
    private static final Logger _logger = Logger.getLogger(AuditConceptToEVS.class.getName());
}