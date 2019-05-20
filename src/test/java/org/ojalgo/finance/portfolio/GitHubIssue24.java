package org.ojalgo.finance.portfolio;

import java.math.BigDecimal;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.ojalgo.TestUtils;
import org.ojalgo.matrix.PrimitiveMatrix;
import org.ojalgo.netio.BasicLogger;
import org.ojalgo.optimisation.Optimisation.State;

/**
 * <a href="https://github.com/optimatika/ojAlgo/issues/24">Issue 24 @ GitHub</a>
 */
public class GitHubIssue24 extends FinancePortfolioTests {

    public static MarkowitzModel buildProblematicMarkowitzModel(final boolean cleanCovariances, final boolean validateOptimisationModel,
            final boolean debugOptimisationSolver) {
        return GitHubIssue24.buildMarkowitzModel(2.5E-5, cleanCovariances, validateOptimisationModel, debugOptimisationSolver);
    }

    static MarkowitzModel buildMarkowitzModel(final double targetVariance, final boolean cleanCovariances, final boolean validateOptimisationModel,
            final boolean debugOptimisationSolver) {

        double[] expectedReturns = { 0.055, 0.02, 0.17300000000000001, 0.094, 0.106, 0.10400000000000001, 0.263, 0.052000000000000005, 0.049, 0.18, 0.139,
                0.121, 0.212, 0.08, 0.1, 0.0, 0.1847, 0.1, 0.098, 0.21300000000000002, 0.203, 0.053, 0.13699999999999998, 0.09699999999999999, 0.087,
                0.054000000000000006, 0.18100000000000002, 0.196, 0.077, 0.21300000000000002, 0.068, 0.034, 0.22100000000000003, 0.20500000000000002, 0.0 };

        double[][] covariance = {
                { 0.002025, 6.949048807368E-4, 2.585792530439999E-4, -1.3919235237E-4, 3.696502341132E-4, 5.708783504934E-4, -2.089910573019E-4,
                        4.7472860499119993E-4, 4.111014323592E-4, -4.276964148282E-4, 4.3828991136E-5, -4.790804086746E-4, -2.31799821336E-5,
                        4.085659242787501E-4, -2.4085515777165002E-4, -8.536531494262498E-4, -2.7910971811980005E-4, 2.91356400519E-4, 4.62907923651E-4,
                        -2.0987205500744998E-4, -4.1490405399555E-4, 2.3044293858600002E-4, 1.7136234036000002E-4, 2.7683496379799998E-5, 2.8674615497849997E-4,
                        2.32616156049E-4, -5.8286886577785E-4, -3.6466038624240004E-4, 2.7055812455999996E-6, -2.818434452616E-4, -1.44395351595E-4,
                        3.2648700503519994E-4, -5.2299662421825E-4, -4.2225471479879996E-4, -3.8432830660380004E-4 },
                { 6.949048807368E-4, 7.398400000000001E-4, 4.432512E-5, 4.7066336000000006E-5, 2.0255568E-4, 2.285888E-6, -8.5738752E-5, 1.7921971200000002E-4,
                        1.68609536E-4, -2.4261856E-5, 5.392128000000001E-5, -3.0804505468153606E-4, 6.2107392E-5, -1.5879360000000003E-5,
                        -1.3169696000000001E-5, -7.931012725507999E-4, -8.240889903531842E-4, 2.4633272868768E-4, -6.3401568E-5, -8.616062400000002E-5,
                        -2.6713310400000004E-4, 1.45328512E-4, -2.45888E-5, -4.360704E-6, 7.467488E-5, 8.767104000000001E-5, -2.7349872000000004E-5,
                        -2.8629632000000002E-5, 1.08403968E-4, 5.1678912000000004E-5, -8.204064E-5, 1.23472768E-4, -2.7602288000000002E-5, -2.8768896E-5,
                        -6.001178758677441E-4 },
                { 2.5857925304399997E-4, 4.432512E-5, 0.0031359999999999995, 3.096016E-4, 2.3844912E-4, 4.239894399999999E-4, 3.951662399999999E-4,
                        3.5054207999999997E-4, 3.3921664000000003E-4, 4.093062399999999E-4, 5.451936E-4, 3.2632710258176E-4, 5.243548799999999E-4, 1.085868E-4,
                        9.048536E-5, 0.002339305361288, 0.00369884735520944, -2.322457942896E-4, 4.8419951999999997E-4, 5.6378784E-4, 5.669260799999999E-4,
                        4.0106303999999995E-4, 1.91296E-4, 1.8180287999999996E-4, 2.7760655999999994E-4, 5.723423999999999E-4, 1.8940152E-4,
                        1.9780992000000003E-4, 2.9448832E-4, 3.459119999999999E-4, -8.27904E-5, 3.4262031999999994E-4, 1.9290319999999998E-4, 1.9540864E-4,
                        0.0025589305141108802 },
                { -1.3919235237E-4, 4.7066336000000006E-5, 3.0960159999999994E-4, 0.00515524, 0.0025230146640000005, 0.002686259144, 0.004914855036,
                        7.022729280000001E-4, 3.710624E-5, 0.004679576488, 0.004540287360000001, 0.001391216089446232, 0.0035419514399999998,
                        0.0027799847100000007, 0.0023106015440000002, 0.005962856033388049, 0.006012899179769049, -3.8760403449888004E-4, 2.9042812800000005E-4,
                        0.004069847298000001, 0.00400969254, 8.93341344E-4, 0.004670446400000001, 0.0029496876, 0.0015869997080000002, 6.195765600000002E-4,
                        0.004838896380000001, 0.005052557384, 6.29645792E-4, 0.0037897045200000006, 0.0015922368000000002, 7.053287359999998E-4, 0.004930546208,
                        0.004999801616, 0.005503445516998417 },
                { 3.696502341132E-4, 2.0255568E-4, 2.3844912E-4, 0.002523014664, 0.0038192400000000002, 0.00377249448, 0.0048973892039999994,
                        6.586594559999999E-4, 1.18171488E-4, 0.003226113264, 0.00315157752, 0.0014857766789758082, 0.002545275024, 0.0018176863200000002,
                        0.001510729428, 0.00324929030771055, 0.004165171444249069, -6.877582915762802E-4, 2.67665688E-4, 0.002937612942, 0.0028765996560000005,
                        7.96295472E-4, 0.0030548976, 0.002704822848, 0.001443118992, 4.6499556000000004E-4, 0.0031640308380000005, 0.0033031259519999997,
                        5.60372736E-4, 0.002626888104, 0.0014724468000000001, 6.27447984E-4, 0.0032237308740000006, 0.003268789872, 0.0037942584325493644 },
                { 5.708783504934E-4, 2.285888E-6, 4.239894399999999E-4, 0.002686259144, 0.0037724944799999998, 0.0058369599999999995, 0.007818603335999999,
                        3.1677273599999993E-4, 7.190156799999999E-4, 0.00417484744, 0.00409378704, 0.001487643418015968, 0.0031042756319999993, 0.00210692864,
                        0.001751067372, 0.0038667327477710997, 0.00533060322315552, -5.949563838372E-4, 7.27398288E-4, 0.004224573144, 0.004229449756,
                        4.0610572799999994E-4, 0.0035351808, 0.0027186451039999996, 0.0012434527839999998, 2.5976000000000005E-4, 0.0036677248680000002,
                        0.0038292810719999996, 2.69343616E-4, 0.002800943184, 0.0016937421599999998, 2.9593998399999997E-4, 0.003737086136,
                        0.0037896294719999995, 0.004614963243655648 },
                { -2.0899105730189997E-4, -8.5738752E-5, 3.951662399999999E-4, 0.004914855036, 0.0048973892039999994, 0.007818603335999999,
                        0.014256359999999997, -1.3760611199999998E-4, 9.148714560000001E-4, 0.008251351919999999, 0.007851170880000001, 0.00247505269947288,
                        0.005768022959999999, 0.004180683540000001, 0.0034747155180000003, 0.00938419600371225, 0.008560553626420803, -9.733318849602001E-4,
                        6.834456E-4, 0.008385967062, 0.008361736026, -9.267350399999999E-5, 0.0070025712, 0.004626033599999999, 0.001704563952, 3.328872E-5,
                        0.007276897476000001, 0.007598052432, -1.26544896E-4, 0.005247171504, 0.00330740388, -1.6200430799999998E-4, 0.0074155769940000005,
                        0.007517853839999999, 0.008925305430074748 },
                { 4.7472860499119993E-4, 1.79219712E-4, 3.505420799999999E-4, 7.022729280000001E-4, 6.586594559999999E-4, 3.16772736E-4, -1.3760611199999996E-4,
                        0.0011289599999999998, 3.6922368E-5, 2.14740288E-4, 4.8347712E-4, 3.84882102443328E-4, 7.506051839999999E-4, 1.319388E-4, 1.09528272E-4,
                        5.150443732223999E-4, 0.0010850164735132803, -1.4610841142688E-4, 5.245168320000001E-4, 1.5916824E-4, 1.1739067200000001E-4,
                        0.001244283264, 2.4111359999999998E-4, 5.276772479999999E-4, 8.29860864E-4, 7.1080128E-4, 2.29515552E-4, 2.397696E-4,
                        8.882872319999999E-4, 7.809500159999998E-4, -2.9516255999999995E-4, 0.0010540837439999998, 2.3429918399999998E-4, 2.3692031999999997E-4,
                        8.45759176656576E-4 },
                { 4.111014323592E-4, 1.68609536E-4, 3.3921664000000003E-4, 3.710624E-5, 1.1817148800000002E-4, 7.190156799999999E-4, 9.148714560000001E-4,
                        3.6922367999999996E-5, 7.398400000000001E-4, 5.86760032E-4, 7.3296384E-4, 1.1547389211667202E-4, 5.16093888E-4, 3.40272E-5, 2.833016E-5,
                        5.35835695192E-5, 6.407889181272642E-4, -7.802065461312002E-5, 6.710359680000002E-4, 9.987864480000001E-4, 9.8647192E-4,
                        3.3053440000000003E-6, 6.98496E-5, 4.7967744000000005E-5, 1.4230496E-5, 6.381120000000001E-6, 5.944641600000001E-5,
                        6.211174400000001E-5, -6.205952E-6, 3.128544E-5, 5.529216000000001E-5, 2.0643712E-5, 6.077568000000001E-5, 6.0980224E-5,
                        3.7687657815184004E-4 },
                { -4.276964148282E-4, -2.4261856000000002E-5, 4.0930624E-4, 0.004679576488, 0.003226113264, 0.00417484744, 0.008251351919999999, 2.14740288E-4,
                        5.86760032E-4, 0.00749956, 0.00720383832, 0.0021147389787618404, 0.005245247688, 0.0038507642600000003, 0.00320081827,
                        0.007084666516355, 0.0076009413059108175, -5.6059927119648E-4, 6.55698828E-4, 0.007535104104, 0.007467923287999999, 2.58677664E-4,
                        0.0064582816, 0.003958593384, 0.001636000436, 2.6911815999999995E-4, 0.006712138242, 0.00700245868, 1.44046976E-4, 0.004822469951999999,
                        0.00264689436, 1.4850341199999998E-4, 0.006829709866, 0.006930694991999999, 0.006975544305596984 },
                { 4.3828991136E-5, 5.392128000000001E-5, 5.451936E-4, 0.004540287360000001, 0.00315157752, 0.00409378704, 0.007851170880000001, 4.8347712E-4,
                        7.329638400000001E-4, 0.00720383832, 0.007056000000000001, 0.0017130710844576005, 0.00521229744, 0.0036382416000000006,
                        0.0030238504800000005, 0.0055564952585730005, 0.007070974385690882, -6.292042441272001E-4, 9.1996128E-4, 0.007345051560000001,
                        0.007273754040000001, 5.100547200000001E-4, 0.00610848, 0.00375838176, 0.0016900060800000003, 4.172616000000001E-4,
                        0.006333316920000001, 0.006611646719999999, 3.3106752E-4, 0.00467983152, 0.0023772672000000003, 3.7168992E-4, 0.00645183,
                        0.006545683199999999, 0.0061431392163741605 },
                { -4.790804086746001E-4, -3.0804505468153606E-4, 3.2632710258176E-4, 0.0013912160894462321, 0.001485776678975808, 0.001487643418015968,
                        0.00247505269947288, 3.84882102443328E-4, 1.1547389211667202E-4, 0.00211473897876184, 0.0017130710844576005, 0.005241760000000001,
                        0.00180045570182928, 0.0021047606344569802, 0.0022141210760340604, 0.0024191327882648003, 0.002610295838108089, 9.690625227031202E-4,
                        6.869581443405601E-5, 0.0019322183410147205, 0.0025395633610678803, 0.001187145566485088, 0.0013611393707648, 0.0015998898832712482,
                        0.001281242614499176, 7.571182064973601E-4, 0.0018835898271284644, 0.0018177771816145444, 9.638599055459201E-4, 0.0016470788079234243,
                        2.9011093325808E-4, 7.29594848430248E-4, 0.0014239370436443881, 0.0016193415789024321, 0.0019427372162138725 },
                { -2.3179982133600002E-5, 6.2107392E-5, 5.243548799999999E-4, 0.0035419514399999998, 0.002545275024, 0.0031042756319999993,
                        0.005768022959999999, 7.506051839999999E-4, 5.16093888E-4, 0.005245247688, 0.00521229744, 0.0018004557018292803, 0.00725904,
                        0.00266403786, 0.00221370474, 0.0049248550073856005, 0.0044432289422590325, -2.1312161940456004E-4, 8.442365759999999E-4,
                        0.005614596504, 0.0052697605799999995, 8.419736639999999E-4, 0.0044672064, 0.002989102272, 0.001577334864, 5.6806248E-4, 0.004638186612,
                        0.005116972272, 5.944642559999999E-4, 0.006927301872, 0.00169483248, 6.5273424E-4, 0.005234405988, 0.005157312767999999,
                        0.004852941704793936 },
                { 4.0856592427875006E-4, -1.587936E-5, 1.0858680000000001E-4, 0.0027799847100000007, 0.0018176863200000002, 0.00210692864, 0.004180683540000001,
                        1.319388E-4, 3.40272E-5, 0.0038507642600000007, 0.0036382416, 0.0021047606344569802, 0.00266403786, 0.004830250000000001,
                        0.0023477100000000002, -0.001157348097951, -4.874152083147201E-4, 1.03023974547E-5, 5.967270000000001E-5, 0.003384105120000001,
                        0.0033465410650000004, 2.0106072000000003E-4, 0.003808600000000001, 0.0021866646, 9.342259500000001E-4, 2.0180020000000004E-4,
                        0.003952717980000001, 0.00412756052, 1.2302055999999998E-4, 0.0027984536400000003, 0.0014306853000000002, 1.3137168E-4,
                        0.004027855125000001, 0.00408444828, -6.828633371281801E-4 },
                { -2.4085515777165E-4, -1.3169696000000001E-5, 9.048536E-5, 0.0023106015440000002, 0.001510729428, 0.001751067372, 0.0034747155180000003,
                        1.0952827200000001E-4, 2.8330160000000003E-5, 0.00320081827, 0.00302385048, 0.0022141210760340604, 0.0022137047400000004,
                        0.0023477100000000002, 0.00316969, 0.004514834585531825, 0.0038592365772498255, 4.7780069830056013E-4, 4.9592418000000006E-5,
                        0.002812489020000001, 0.002781668148, 1.6706687200000001E-4, 0.0031654112, 0.0018172378880000001, 7.76621342E-4, 1.6768392000000002E-4,
                        0.0032852637660000005, 0.0034305008760000004, 1.02141712E-4, 0.0023259489239999998, 0.0011890560000000002, 1.0924226800000001E-4,
                        0.003347765211000001, 0.00339470984, 0.0034929617986369866 },
                { -8.536531494262499E-4, -7.931012725508E-4, 0.002339305361288, 0.00596285603338805, 0.00324929030771055, 0.0038667327477711,
                        0.00938419600371225, 5.150443732224E-4, 5.3583569519199994E-5, 0.007084666516355, 0.005556495258573, 0.0024191327882648003,
                        0.0049248550073856005, -0.001157348097951, 0.004514834585531825, 0.00855625, 0.006281118146305551, -3.9317177466E-5, 7.9509023707125E-4,
                        0.0077290180774454995, 0.0073459587538590755, -6.527776322256E-4, 0.00434351060376, 0.0043806006628145005, 4.693884706266E-4,
                        -0.0014642374546939999, 0.006612876198641776, 0.0073706675048385, -2.086903269664E-4, 0.0043605463982127005, 0.0031247558093069998,
                        -3.607023300103E-4, 0.007065181240283075, 0.0072209666113863995, 0.0066097064693634005 },
                { -2.7910971811980005E-4, -8.240889903531842E-4, 0.0036988473552094403, 0.006012899179769049, 0.004165171444249069, 0.005330603223155521,
                        0.008560553626420803, 0.00108501647351328, 6.407889181272642E-4, 0.0076009413059108175, 0.007070974385690882, 0.0026102958381080885,
                        0.0044432289422590325, -4.874152083147201E-4, 0.003859236577249825, 0.006281118146305551, 0.018117160000000007, -7.905717588456003E-5,
                        0.0013995588290178843, 0.007443078107788029, 0.007485775844949082, -2.74916203826432E-4, 0.006280391877043201, 0.005885116385838425,
                        7.637449010334082E-4, -0.0011269452556816803, 0.007179163793436392, 0.007402915316410002, 8.637375121664E-6, 0.0033461031001056248,
                        0.003033256302456601, -7.24853397848504E-4, 0.006341937807025214, 0.0068925919585241455, 0.00707173154906265 },
                { 2.91356400519E-4, 2.4633272868768006E-4, -2.3224579428960003E-4, -3.8760403449888004E-4, -6.877582915762801E-4, -5.949563838372001E-4,
                        -9.733318849602002E-4, -1.4610841142688E-4, -7.802065461312002E-5, -5.6059927119648E-4, -6.292042441272001E-4, 9.690625227031202E-4,
                        -2.1312161940456E-4, 1.0302397454700002E-5, 4.778006983005601E-4, -3.931717746600001E-5, -7.905717588456003E-5, 0.0029160000000000006,
                        -1.8009222491340002E-4, -5.253159746300401E-4, -6.004801077143401E-4, 5.810848636320001E-6, 6.4390309344E-5, -6.788420741599201E-4,
                        -2.6651258943120003E-4, 1.1244289415040002E-4, -3.792885344319601E-4, -1.4354307007560002E-4, -2.7781679364768003E-4,
                        -3.4179742549440006E-5, -8.400348705672001E-4, 1.5889612197096E-4, -2.9362861307304E-4, -2.2971326472288E-4, -5.081519253470401E-4 },
                { 4.6290792365099995E-4, -6.3401568E-5, 4.8419951999999997E-4, 2.9042812800000005E-4, 2.67665688E-4, 7.27398288E-4, 6.834456E-4, 5.24516832E-4,
                        6.710359680000001E-4, 6.55698828E-4, 9.199612800000001E-4, 6.869581443405601E-5, 8.44236576E-4, 5.967270000000001E-5,
                        4.9592418000000006E-5, 7.9509023707125E-4, 0.0013995588290178843, -1.8009222491340005E-4, 0.00101124, 0.001105007706, 0.001149802458,
                        4.84413216E-4, 1.0786560000000001E-4, 1.88632512E-4, 3.25165176E-4, 2.8781544000000005E-4, 1.0385339400000001E-4, 1.0835659200000001E-4,
                        3.5809344000000006E-4, 3.1293108E-4, -1.2760704000000002E-4, 4.0983839999999996E-4, 1.0539664800000002E-4, 1.0751452800000002E-4,
                        0.0011156932373580241 },
                { -2.0987205500744998E-4, -8.6160624E-5, 5.6378784E-4, 0.004069847298000001, 0.002937612942, 0.004224573144, 0.008385967062,
                        1.5916824000000002E-4, 9.987864480000001E-4, 0.007535104104, 0.00734505156, 0.0019322183410147205, 0.005614596504,
                        0.0033841051200000005, 0.0028124890200000004, 0.0077290180774455, 0.007443078107788029, -5.253159746300401E-4, 0.001105007706,
                        0.009158490000000002, 0.008352701742, 1.2604838399999998E-4, 0.0057519528, 0.0037244219760000003, 0.001477759206, 1.5195246000000002E-4,
                        0.0060384623310000005, 0.006821469204000001, 4.7895936E-5, 0.004641867252, 0.0026553304800000003, 4.0084902000000005E-5,
                        0.007129517934000001, 0.0068128830000000005, 0.007319471442058778 },
                { -4.1490405399554996E-4, -2.6713310400000004E-4, 5.669260799999999E-4, 0.00400969254, 0.0028765996560000005, 0.004229449756, 0.008361736026,
                        1.17390672E-4, 9.864719200000003E-4, 0.007467923288, 0.007273754040000001, 0.0025395633610678803, 0.00526976058, 0.0033465410650000004,
                        0.002781668148, 0.007345958753859076, 0.007485775844949082, -6.004801077143401E-4, 0.001149802458, 0.008352701742000002, 0.00840889,
                        9.238224800000001E-5, 0.005601036, 0.0036888122319999996, 0.001446868276, 1.3001226000000003E-4, 0.005832554658000001, 0.0060938318,
                        2.3284464E-5, 0.004239246984, 0.00254737098, 1.2146582E-5, 0.005955599552, 0.00600172832, 0.0071714939579687045 },
                { 2.3044293858600002E-4, 1.45328512E-4, 4.0106303999999995E-4, 8.93341344E-4, 7.962954719999999E-4, 4.0610572799999994E-4,
                        -9.267350399999999E-5, 0.001244283264, 3.305344E-6, 2.58677664E-4, 5.100547200000001E-4, 0.0011871455664850881, 8.419736639999999E-4,
                        2.0106072000000003E-4, 1.6706687200000001E-4, -6.527776322256E-4, -2.74916203826432E-4, 5.81084863632E-6, 4.84413216E-4, 1.26048384E-4,
                        9.238224800000001E-5, 0.00153664, 3.628352E-4, 8.156814399999999E-4, 0.001063607328, 8.4446208E-4, 3.5018340000000003E-4,
                        3.6574854400000004E-4, 0.001061868416, 0.001032344544, -2.4578400000000003E-4, 0.001301897072, 3.56923056E-4, 3.6145535999999997E-4,
                        -3.85154572883808E-4 },
                { 1.7136234036E-4, -2.45888E-5, 1.9129599999999998E-4, 0.004670446400000001, 0.0030548976, 0.0035351808, 0.007002571199999999,
                        2.4111359999999998E-4, 6.98496E-5, 0.0064582816, 0.006108480000000001, 0.0013611393707648, 0.0044672064, 0.003808600000000001,
                        0.0031654112, 0.00434351060376, 0.006280391877043201, 6.4390309344E-5, 1.0786560000000001E-4, 0.0057519528000000006, 0.005601036,
                        3.628352E-4, 0.0064, 0.0036707936, 0.0015780352, 3.568640000000001E-4, 0.006626726400000001, 0.0069169248, 2.1991680000000002E-4,
                        0.004692816, 0.0024029280000000004, 2.543232E-4, 0.0067463984, 0.0068581056, 0.004762076594496001 },
                { 2.7683496379799998E-5, -4.360704E-6, 1.8180287999999999E-4, 0.0029496876, 0.002704822848, 0.0027186451039999996, 0.004626033599999999,
                        5.276772479999999E-4, 4.7967744000000005E-5, 0.003958593384, 0.00375838176, 0.0015998898832712482, 0.002989102272,
                        0.0021866646000000003, 0.0018172378880000003, 0.0043806006628145005, 0.005885116385838425, -6.788420741599201E-4, 1.88632512E-4,
                        0.003724421976, 0.003688812232, 8.156814399999999E-4, 0.0036707935999999997, 0.00446224, 0.0020841025519999995, 3.4204272E-4,
                        0.0038063755560000002, 0.00397435952, 5.02955904E-4, 0.00313309368, 0.0025579857600000005, 6.251224160000001E-4, 0.003878840196,
                        0.003932414463999999, 0.004509763010656697 },
                { 2.8674615497849997E-4, 7.467488E-5, 2.7760655999999994E-4, 0.0015869997080000002, 0.001443118992, 0.0012434527839999998, 0.001704563952,
                        8.29860864E-4, 1.4230496E-5, 0.001636000436, 0.00169000608, 0.0012812426144991759, 0.001577334864, 9.342259500000001E-4, 7.76621342E-4,
                        4.693884706266E-4, 7.637449010334082E-4, -2.6651258943120003E-4, 3.25165176E-4, 0.001477759206, 0.001446868276, 0.001063607328,
                        0.0015780352, 0.0020841025519999995, 0.00268324, 5.697482E-4, 0.0016267007820000001, 0.001698517856, 0.0011481283519999999,
                        0.00173445048, 8.3555472E-4, 8.70474136E-4, 0.0016575207459999998, 0.0016806282079999998, 3.9609092637332395E-4 },
                { 2.3261615604900003E-4, 8.767104000000001E-5, 5.723423999999999E-4, 6.195765600000002E-4, 4.6499556000000004E-4, 2.5976E-4,
                        3.328872000000001E-5, 7.1080128E-4, 6.38112E-6, 2.6911815999999995E-4, 4.1726160000000003E-4, 7.571182064973602E-4,
                        5.680624800000001E-4, 2.0180020000000007E-4, 1.6768392E-4, -0.001464237454694, -0.0011269452556816803, 1.1244289415040002E-4,
                        2.8781544000000005E-4, 1.5195246000000002E-4, 1.3001226E-4, 8.444620800000001E-4, 3.568640000000001E-4, 3.4204272E-4,
                        5.697482000000001E-4, 0.0011560000000000001, 3.5147976E-4, 3.6696880000000007E-4, 6.2097056E-4, 6.8625192E-4, -2.0397960000000003E-4,
                        7.2143444E-4, 3.5800674000000004E-4, 3.6299216000000004E-4, -0.0012151819283578002 },
                { -5.8286886577785E-4, -2.7349872000000004E-5, 1.8940152E-4, 0.004838896380000001, 0.0031640308380000005, 0.003667724868, 0.007276897476,
                        2.29515552E-4, 5.944641600000001E-5, 0.006712138242, 0.006333316920000001, 0.0018835898271284644, 0.004638186612, 0.003952717980000001,
                        0.0032852637660000005, 0.006612876198641775, 0.007179163793436393, -3.792885344319601E-4, 1.0385339400000002E-4, 0.006038462331000001,
                        0.005832554658000001, 3.501834E-4, 0.0066267264000000005, 0.0038063755560000002, 0.0016267007820000004, 3.5147976E-4,
                        0.006905610000000001, 0.0071968156680000004, 2.1406560000000003E-4, 0.004869706536, 0.0024905568600000006, 2.28775962E-4,
                        0.007018652592000001, 0.00711033516, 0.006370533016519621 },
                { -3.6466038624240004E-4, -2.8629632E-5, 1.9780992E-4, 0.005052557384000001, 0.003303125952, 0.0038292810719999996, 0.007598052432, 2.397696E-4,
                        6.211174400000001E-5, 0.0070024586799999995, 0.00661164672, 0.0018177771816145442, 0.005116972272000001, 0.00412756052,
                        0.0034305008760000004, 0.0073706675048385, 0.007402915316410002, -1.4354307007560002E-4, 1.08356592E-4, 0.006821469204000001,
                        0.0060938318, 3.65748544E-4, 0.006916924800000001, 0.00397435952, 0.001698517856, 3.669688E-4, 0.007196815668000001,
                        0.007956640000000001, 2.2354233599999998E-4, 0.0053928464640000005, 0.00268986168, 2.38863328E-4, 0.008140090504, 0.00800723424,
                        0.0066969553054869934 },
                { 2.7055812455999996E-6, 1.0840396800000001E-4, 2.9448831999999994E-4, 6.29645792E-4, 5.60372736E-4, 2.69343616E-4, -1.26544896E-4,
                        8.88287232E-4, -6.205952E-6, 1.4404697599999997E-4, 3.3106752000000003E-4, 9.6385990554592E-4, 5.94464256E-4, 1.2302056E-4,
                        1.02141712E-4, -2.086903269664E-4, 8.637375121664E-6, -2.778167936476801E-4, 3.5809344E-4, 4.7895936E-5, 2.3284464E-5, 0.001061868416,
                        2.199168E-4, 5.02955904E-4, 0.0011481283519999999, 6.2097056E-4, 2.1406560000000003E-4, 2.23542336E-4, 0.00135424, 7.3837728E-4,
                        -2.2199231999999998E-4, 8.924338559999999E-4, 2.1824129600000002E-4, 2.2122688E-4, -1.5727884198208E-4 },
                { -2.818434452616E-4, 5.1678912000000004E-5, 3.459119999999999E-4, 0.0037897045200000006, 0.002626888104, 0.002800943184, 0.005247171504,
                        7.809500159999999E-4, 3.128544E-5, 0.004822469951999999, 0.00467983152, 0.0016470788079234243, 0.006927301872, 0.0027984536400000003,
                        0.002325948924, 0.0043605463982127, 0.0033461031001056248, -3.417974254944E-5, 3.1293108E-4, 0.004641867252000001, 0.004239246984,
                        0.001032344544, 0.004692816, 0.0031330936799999994, 0.00173445048, 6.862519199999999E-4, 0.004869706536, 0.005392846464,
                        7.383772799999999E-4, 0.00725904, 0.00168864696, 8.031071279999999E-4, 0.005531860488, 0.005455383264, 0.004083169149099768 },
                { -1.44395351595E-4, -8.204064000000001E-5, -8.27904E-5, 0.0015922368000000002, 0.0014724468, 0.0016937421599999998, 0.00330740388,
                        -2.9516256E-4, 5.529216E-5, 0.0026468943599999998, 0.0023772672000000003, 2.9011093325808E-4, 0.00169483248, 0.0014306853000000002,
                        0.001189056, 0.003124755809307, 0.003033256302456601, -8.400348705672002E-4, -1.2760704000000002E-4, 0.0026553304800000003,
                        0.00254737098, -2.4578400000000003E-4, 0.002402928, 0.0025579857600000005, 8.3555472E-4, -2.0397960000000003E-4, 0.00249055686,
                        0.00268986168, -2.2199232E-4, 0.00168864696, 0.0043560000000000005, -2.3817024E-4, 0.002703624, 0.00269442624, 0.00288619107186696 },
                { 3.264870050352E-4, 1.23472768E-4, 3.4262031999999994E-4, 7.05328736E-4, 6.27447984E-4, 2.9593998399999997E-4, -1.62004308E-4,
                        0.0010540837439999998, 2.0643712E-5, 1.4850341199999998E-4, 3.7168992000000004E-4, 7.295948484302481E-4, 6.5273424E-4, 1.3137168E-4,
                        1.09242268E-4, -3.607023300103E-4, -7.24853397848504E-4, 1.5889612197096E-4, 4.0983839999999996E-4, 4.0084902000000005E-5,
                        1.2146581999999999E-5, 0.001301897072, 2.543232E-4, 6.251224160000001E-4, 8.70474136E-4, 7.2143444E-4, 2.28775962E-4,
                        2.3886332799999998E-4, 8.924338559999999E-4, 8.03107128E-4, -2.3817024E-4, 0.00128164, 2.333086E-4, 2.3625135999999996E-4,
                        -3.18539969399288E-4 },
                { -5.2299662421825E-4, -2.7602288000000002E-5, 1.9290319999999998E-4, 0.004930546208000001, 0.003223730874, 0.0037370861359999996,
                        0.007415576994, 2.3429918399999998E-4, 6.077568000000001E-5, 0.006829709866, 0.00645183, 0.0014239370436443881, 0.005234405988,
                        0.004027855125000001, 0.0033477652110000004, 0.007065181240283075, 0.006341937807025214, -2.9362861307304E-4, 1.05396648E-4,
                        0.007129517934000001, 0.005955599552, 3.56923056E-4, 0.006746398400000001, 0.0038788401960000002, 0.001657520746, 3.5800674000000004E-4,
                        0.007018652592000001, 0.008140090504, 2.1824129600000002E-4, 0.005531860488, 0.002703624, 2.3330860000000004E-4, 0.008667610000000001,
                        0.008320294864, 0.0063299146122372165 },
                { -4.2225471479879996E-4, -2.8768895999999997E-5, 1.9540864E-4, 0.004999801616, 0.003268789872, 0.003789629471999999, 0.007517853839999999,
                        2.3692031999999997E-4, 6.0980223999999995E-5, 0.006930694992, 0.006545683199999999, 0.001619341578902432, 0.005157312767999999,
                        0.00408444828, 0.00339470984, 0.0072209666113863995, 0.0068925919585241455, -2.2971326472288002E-4, 1.07514528E-4, 0.006812883,
                        0.00600172832, 3.6145535999999997E-4, 0.0068581056, 0.003932414463999999, 0.0016806282079999998, 3.6299216E-4, 0.00711033516,
                        0.00800723424, 2.2122688E-4, 0.005455383264, 0.0026944262399999997, 2.3625135999999998E-4, 0.008320294863999999, 0.00817216,
                        0.0064944308553585755 },
                { -3.843283066038E-4, -6.001178758677442E-4, 0.00255893051411088, 0.005503445516998416, 0.0037942584325493644, 0.0046149632436556474,
                        0.008925305430074748, 8.45759176656576E-4, 3.7687657815184004E-4, 0.006975544305596984, 0.0061431392163741605, 0.0019427372162138725,
                        0.004852941704793936, -6.828633371281801E-4, 0.0034929617986369866, 0.0066097064693634005, 0.00707173154906265, -5.081519253470401E-4,
                        0.0011156932373580241, 0.007319471442058778, 0.007171493957968705, -3.85154572883808E-4, 0.004762076594496001, 0.004509763010656696,
                        3.9609092637332395E-4, -0.0012151819283578002, 0.00637053301651962, 0.006696955305486993, -1.5727884198208E-4, 0.004083169149099768,
                        0.00288619107186696, -3.18539969399288E-4, 0.006329914612237217, 0.006494430855358576, 0.0064963600000000005 } };

        MarketEquilibrium marketEquilibrium = new MarketEquilibrium(PrimitiveMatrix.FACTORY.rows(covariance));

        if (cleanCovariances) {
            marketEquilibrium = marketEquilibrium.clean();
        }

        MarkowitzModel retVal = new MarkowitzModel(marketEquilibrium, PrimitiveMatrix.FACTORY.rows(expectedReturns));

        retVal.optimiser().debug(debugOptimisationSolver).validate(validateOptimisationModel);

        retVal.setTargetVariance(BigDecimal.valueOf(targetVariance));

        for (int index = 0; index < expectedReturns.length; index++) {
            retVal.setLowerLimit(index, BigDecimal.ZERO);
            retVal.setUpperLimit(index, BigDecimal.ONE);
        }

        return retVal;
    }

    @Test
    @Disabled("Google Finance stopped working")
    public void testHanging() throws Exception {

        MarkowitzModel markowitzModel = GitHubIssue24.buildMarkowitzModel(2.5E-5, false, false, false);

        double tmpMeanReturn = markowitzModel.getMeanReturn();
        if (DEBUG) {
            BasicLogger.debug(tmpMeanReturn);
        }

        TestUtils.assertTrue(markowitzModel.optimiser().getState().isOptimal()); // Won't reach here...
    }

    @Test
    public void testOriginallyHangingButNowCleaned() throws Exception {

        MarkowitzModel markowitzModel = GitHubIssue24.buildMarkowitzModel(2.5E-5, true, false, false);

        double tmpMeanReturn = markowitzModel.getMeanReturn();
        if (DEBUG) {
            BasicLogger.debug(tmpMeanReturn);
        }

        TestUtils.assertTrue(markowitzModel.optimiser().getState().isOptimal()); // Won't reach here...
    }

    @Test
    public void testP20160705() {

        MarkowitzModel tmpModel = GitHubIssue24.buildProblematicMarkowitzModel(true, true, DEBUG);

        tmpModel.getWeights();

        TestUtils.assertTrue(tmpModel.optimiser().getState().isFeasible());
    }

    @Test
    public void testSuccess() throws Exception {

        MarkowitzModel markowitzModel = GitHubIssue24.buildMarkowitzModel(0.015, false, false, false);

        double meanReturn = markowitzModel.getMeanReturn();
        if (DEBUG) {
            BasicLogger.debug(meanReturn);
        }

        State optimisationState = markowitzModel.optimiser().getState();
        TestUtils.assertTrue(optimisationState.isOptimal());
    }

}
