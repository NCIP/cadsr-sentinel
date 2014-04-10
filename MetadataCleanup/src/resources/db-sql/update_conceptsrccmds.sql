/*L
  Copyright ScenPro Inc, SAIC-F

  Distributed under the OSI-approved BSD 3-Clause License.
  See http://ncip.github.com/cadsr-sentinal/LICENSE.txt for details.
L*/

update sbrext.concepts_view_ext set evs_source = 'NCI_CONCEPT_CODE' WHERE preferred_name like 'C%' and (evs_source like 'RADLEX_CODE' or  evs_source like 'CTCAE_CODE');


update sbrext.concepts_view_ext set evs_source = 'CTCAE_CODE' where preferred_name = 'E10104' and evs_source like 'UNKNOWN SOURCE';


update sbrext.concepts_view_ext set preferred_name = 'N0000135717' where preferred_name = 'C30468' and evs_source like 'VA_NDF_CODE';

update sbrext.concepts_view_ext set preferred_name = 'N0000135749' where preferred_name = 'C30366' and evs_source like 'VA_NDF_CODE';

update sbrext.concepts_view_ext set preferred_name = 'N0000135758' where preferred_name = 'C30514' and evs_source like 'VA_NDF_CODE';

update sbrext.concepts_view_ext set preferred_name = 'N0000135726' where preferred_name = 'C30406' and evs_source like 'VA_NDF_CODE';

update sbrext.concepts_view_ext set preferred_name = 'N0000135765' where preferred_name = 'C30524' and evs_source like 'VA_NDF_CODE';

update sbrext.concepts_view_ext set preferred_name = 'N0000009802' where preferred_name = 'C29970' and evs_source like 'VA_NDF_CODE';

update sbrext.concepts_view_ext set preferred_name = 'N0000000003' where preferred_name = 'C180' and evs_source like 'VA_NDF_CODE';

update sbrext.concepts_view_ext set preferred_name = 'N0000000012' where preferred_name = 'C202' and evs_source like 'VA_NDF_CODE';

update sbrext.concepts_view_ext set preferred_name = 'N0000009772' where preferred_name = 'C29910' and evs_source like 'VA_NDF_CODE';

update sbrext.concepts_view_ext set preferred_name = 'N0000009770' where preferred_name = 'C29906' and evs_source like 'VA_NDF_CODE';

update sbrext.concepts_view_ext set preferred_name = 'N0000000017' where preferred_name = 'C212' and evs_source like 'VA_NDF_CODE';

update sbrext.concepts_view_ext set preferred_name = 'N0000005817' where preferred_name = 'C21978' and evs_source like 'VA_NDF_CODE';

update sbrext.concepts_view_ext set preferred_name = 'N0000135800' where preferred_name = 'C53832' and evs_source like 'VA_NDF_CODE';

update sbrext.concepts_view_ext set preferred_name = 'N0000009933' where preferred_name = 'C30234' and evs_source like 'VA_NDF_CODE';

update sbrext.concepts_view_ext set preferred_name = 'N0000000055' where preferred_name = 'C288' and evs_source like 'VA_NDF_CODE'; 

update sbrext.concepts_view_ext set preferred_name = 'N0000010197' where preferred_name = 'C31750' and evs_source like 'VA_NDF_CODE';

update sbrext.concepts_view_ext set preferred_name = 'N0000135799' where preferred_name = 'C30552' and evs_source like 'VA_NDF_CODE';

update sbrext.concepts_view_ext set preferred_name = 'N0000135762' where preferred_name = 'C30518' and evs_source like 'VA_NDF_CODE';

update sbrext.concepts_view_ext set preferred_name = 'N0000000223' where preferred_name = 'C624' and evs_source like 'VA_NDF_CODE';

update sbrext.concepts_view_ext set preferred_name = 'N0000007949' where preferred_name = 'C30356' and evs_source like 'VA_NDF_CODE';

update sbrext.concepts_view_ext set preferred_name = 'N0000000016' where preferred_name = 'C210' and evs_source like 'VA_NDF_CODE';

update sbrext.concepts_view_ext set preferred_name = 'N0000000022' where preferred_name = 'C222' and evs_source like 'VA_NDF_CODE';

update sbrext.concepts_view_ext set preferred_name = 'N0000006806' where preferred_name = 'C23962' and evs_source like 'VA_NDF_CODE';

update sbrext.concepts_view_ext set preferred_name = 'N0000009057' where preferred_name = 'C28480' and evs_source like 'VA_NDF_CODE';

update sbrext.concepts_view_ext set preferred_name = 'N0000007833' where preferred_name = 'C26026' and evs_source like 'VA_NDF_CODE';

update sbrext.concepts_view_ext set preferred_name = 'N0000009912' where preferred_name = 'C30190' and evs_source like 'VA_NDF_CODE';

update sbrext.concepts_view_ext set preferred_name = 'N0000135782' where preferred_name = 'C30404' and evs_source like 'VA_NDF_CODE';

update sbrext.concepts_view_ext set preferred_name = 'N0000010016' where preferred_name = 'C30576' and evs_source like 'VA_NDF_CODE';

update sbrext.concepts_view_ext set preferred_name = 'N0000135725' where preferred_name = 'C30476' and evs_source like 'VA_NDF_CODE';

update sbrext.concepts_view_ext set preferred_name = 'N0000009932' where preferred_name = 'C30232' and evs_source like 'VA_NDF_CODE';

update sbrext.concepts_view_ext set preferred_name = 'N0000009749' where preferred_name = 'C29864' and evs_source like 'VA_NDF_CODE';

update sbrext.concepts_view_ext set preferred_name = 'N0000000008' where preferred_name = 'C194' and evs_source like 'VA_NDF_CODE';

update sbrext.concepts_view_ext set preferred_name = 'N0000007363' where preferred_name = 'C25078' and evs_source like 'VA_NDF_CODE';

update sbrext.concepts_view_ext set preferred_name = 'N0000001668' where preferred_name = 'C3514' and evs_source like 'VA_NDF_CODE';

update sbrext.concepts_view_ext set preferred_name = 'N0000009925' where preferred_name = 'C30216' and evs_source like 'VA_NDF_CODE';

update sbrext.concepts_view_ext set preferred_name = 'N0000000015' where preferred_name = 'C208' and evs_source like 'VA_NDF_CODE';

update sbrext.concepts_view_ext set preferred_name = 'N0000010025' where preferred_name = 'C30648' and evs_source like 'VA_NDF_CODE';

update sbrext.concepts_view_ext set preferred_name = 'N0000135778' where preferred_name = 'C30602' and evs_source like 'VA_NDF_CODE';

update sbrext.concepts_view_ext set preferred_name = 'N0000000009' where preferred_name = 'C196' and evs_source like 'VA_NDF_CODE';

update sbrext.concepts_view_ext set preferred_name = 'N0000135716' where preferred_name = 'C53796' and evs_source like 'VA_NDF_CODE';

update sbrext.concepts_view_ext set preferred_name = 'N0000135759' where preferred_name = 'C53824' and evs_source like 'VA_NDF_CODE';

update sbrext.concepts_view_ext set preferred_name = 'N0000135740' where preferred_name = 'C30500' and evs_source like 'VA_NDF_CODE';

commit;