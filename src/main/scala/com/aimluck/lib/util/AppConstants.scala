/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aimluck.lib.util

import java.io.File
import java.io.FileNotFoundException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.TimeZone
import org.dotme.liquidtpl.LanguageUtil
import scala.util.matching.Regex
import com.aimluck.lib.beans.PlanBean

object AppConstants {
  val CONFIG_FOLDER = "contents"
  val CONFIG_FILE = "app"
  val SYSTEM_TIME_ZONE: TimeZone = TimeZone.getTimeZone("GMT-8:00")

  val SUPPORT_MAIL = "support@aimluck.com"
  val SUPPORT_MAIL2 = "takase@aimluck.com"

  val USER_STATE_ENABLE = "enable"
  val USER_STATE_PAUSE = "pause"
  
  val USER_STATE_LIST = List(USER_STATE_ENABLE, USER_STATE_PAUSE)

  // settings for validate
  val VALIDATE_STRING_LENGTH = 100
  val VALIDATE_LONGTEXT_LENGTH = 10000

  // settings for check-alive
  val DEFAULT_SENDER = "CloudDoctor <admin@clouddoctor.jp>"
  val DEFAULT_TIMEOUT_SECONDS = 120
  val DATA_LIMIT_RECIPIENTS_PER_CHECK = 10
  val DATA_LIMIT_THRESHOLD = 60

  val ICON_SRC_LIST = "/img/icon/16/notepad.gif"
  val ICON_SRC_EDIT = "/img/icon/16/pencil.gif"
  val ICON_SRC_DELETE = "/img/icon/16/button-delete.gif"
  val IMG_SRC_INDICATOR = "/img/common/ajax-loader.gif"

  val BANNED_DOMAIN_REGEX = "(.*(zoho\\.com|auone\\.jp|aitai\\.ne\\.jp|akari\\.ne\\.jp|alpha-net\\.ne\\.jp|asahi-net\\.or\\.jp|auone-net\\.jp|bai\\.ne\\.jp|banban\\.ne\\.jp|bb-east\\.ne\\.jp|bb-west\\.ne\\.jp|bbexcite\\.jp|bbiq\\.jp|biglobe\\.ne\\.jp|ccnw\\.ne\\.jp|dion\\.ne\\.jp|dream\\.jp|dti\\.ne\\.jp|dti2\\.ne\\.jp|e23\\.jp|email\\.ne\\.jp|eonet\\.ne\\.jp|fiberbit\\.net|gol\\.com|gyao\\.ne\\.jp|h555\\.net|hi-ho\\.ne\\.jp|highway\\.ne\\.jp|home\\.ne\\.jp|iam\\.ne\\.jp|iij\\.ad\\.jp|iij4u\\.or\\.jp|iijmio-mail\\.jp|infoweb\\.ne\\.jp|interq\\.or\\.jp|isao\\.net|itscom\\.net|jrjr\\.jp|jrnet\\.ne\\.jp|katch\\.ne\\.jp|kcn\\.jp|kiwi\\.ne\\.jp|kojima\\.net|megaegg\\.ne\\.jp|migliore\\.co\\.jp|miobox\\.jp|miomio\\.jp|nava21\\.ne\\.jp|ncv\\.ne\\.jp|nexyzbb\\.ne\\.jp|nifty\\.com|nifty\\.ne\\.jp|ocn\\.ne\\.jp|odn\\.ne\\.jp|plala\\.or\\.jp|point\\.ne\\.jp|reset\\.jp|rim\\.or\\.jp|rimnet\\.ne\\.jp|sannet\\.ne\\.jp|scn\\.tv|sl-d51\\.jp|so-net\\.ne\\.jp|speednet\\.ne\\.jp|spinnet\\.jp|t-com\\.ne\\.jp|thn\\.ne\\.jp|tiki\\.ne\\.jp|tnc\\.ne\\.jp|ttcn\\.ne\\.jp|u-netsurf\\.jp|urban\\.ne\\.jp|wakwak\\.com|watv\\.ne\\.jp|x-il\\.jp|ybb\\.ne\\.jp|yomogi\\.jp|zaq\\.ne\\.jp|ztv\\.ne\\.jp|mediatti\\.net|pikara\\.ne\\.jp|csf\\.ne\\.jp|hinocatv\\.ne\\.jp|tamatele\\.ne\\.jp|tees\\.ne\\.jp|kcn-tv\\.ne\\.jp|commufa\\.jp|tees\\.jp|gate01\\.com|pbc\\.ne\\.jp|wcv\\.jp|warabi\\.ne\\.jp|c3-net\\.ne\\.jp|anc-tv\\.ne\\.jp|ii-okinawa\\.ne\\.jp|fctv\\.ne\\.jp|ogaki-tv\\.ne\\.jp|hokkai\\.net|ogaki-tv\\.ne\\.jp|leo-net\\.jp|cyberhome\\.ne\\.jp|bunbun\\.ne\\.jp|pdx\\.ne\\.jp|nirai\\.ne\\.jp|chukai\\.ne\\.jp|m-cn\\.ne\\.jp|takauji\\.or\\.jp|kbn\\.ne\\.jp|ctb\\.ne\\.jp|jway\\.ne\\.jp|omn\\.ne\\.jp|e-catv\\.ne\\.jp|sakura\\.ne\\.jp|e-mansion\\.com|ncn-t\\.net|hb\\.tp1\\.jp|ctt\\.ne\\.jp|canvas\\.ne\\.jp|seikyou\\.ne\\.jp|mctv\\.ne\\.jp|mopera\\.net|catv296\\.ne\\.jp|b-star\\.jp|across\\.or\\.jp|sec-i\\.co\\.jp|kamakuranet\\.ne\\.jp|tcnet\\.ne\\.jp|a-net\\.ne\\.jp|nns\\.ne\\.jp|hokkaidou\\.me|ingz\\.co\\.jp|emobile\\.ne\\.jp|i-ml\\.com|qit\\.ne\\.jp|i\\.softbank\\.jp|ccn4\\.aitai\\.ne\\.jp|enjoy\\.ne\\.jp|tokai\\.or\\.jp|actv\\.ne\\.jp|next\\.ne\\.jp|scmg\\.ne\\.jp|scmg\\.co\\.jp|biglobe\\.jp|au-hikari\\.ne\\.jp|jcom\\.home\\.ne\\.jp|iwamicatv\\.jp|0120\\.ro|070\\.jp|090\\.ac|0ad\\.zzn\\.com|104\\.net|117\\.cx|15jam\\.jp|163\\.com|1coolplace\\.com|1dk\\.jp|1funplace\\.com|1internetdrive\\.com|1kg\\.jp|1mg\\.jp|1musicrow\\.com|1netdrive\\.com|1nsyncfan\\.com|1under\\.com|1webave\\.com|1webhighway\\.com|200v\\.ro|21cn\\.com|24\\.am|24h\\.co\\.jp|25cent\\.net|263\\.net|2911\\.net|2cv\\.org|2die4\\.com|3251web\\.com|360\\.cc|363\\.net|3bk\\.net|3rsoft\\.com|557188\\.com|70\\.ro|707\\.to|777\\.ac|777\\.net\\.cn|80\\.fm|888mail\\.net|a-gata\\.com|aamail\\.jp|ab-gata\\.com|abe3\\.net|abeasami\\.com|ac\\.fm|accountant\\.com|ace-of-base\\.com|adexec\\.com|adres\\.ath\\.cx|africamail\\.com|ahmegami\\.net|ai\\.to|aitakahashi\\.com|ajishisato\\.zzn\\.com|akari\\.ne\\.jp|alabama\\.co\\.jp|alaska\\.co\\.jp|all-mychildren\\.com|allergist\\.com|alumnidirector\\.com|am\\.ai|am\\.to\\.fm|amakudari\\.com|ambition\\.ath\\.cx|america\\.co\\.jp|american\\.co\\.jp|amethysts\\.zzn\\.com|amuro\\.net|amuromail\\.com|anan\\.to|andylau\\.net|anet\\.ne\\.jp|animefan\\.net|animefan\\.org|animejunction\\.com|anmail\\.com|another-world\\.com|antisocial\\.com|anywhereusa\\.com|aoki3\\.net|aqua\\.livedoor\\.com|aquamarine\\.zzn\\.com|arashi\\.net|archaeologist\\.com|arcticmail\\.com|aries\\.livedoor\\.com|arita3\\.net|arizona\\.co\\.jp|arkansas\\.co\\.jp|artlover\\.com|as-if\\.com|asheville\\.com|asia\\.com|asianavenue\\.com|asianoffice\\.com|astrosfan\\.net|atarime\\.net|athenachu\\.net|atlantaoffice\\.com|atlas\\.sk|austinoffice\\.com|australiamail\\.com|av\\.fm|b-dash\\.net|b-gata\\.com|babylon5fan\\.com|backstreet-boys\\.com|backstreetboysclub\\.com|badboy\\.co\\.jp|badgirl\\.co\\.jp|bagus\\.to|bakudan\\.net|baldandsexy\\.com|bali\\.fm|balloon\\.cc|bashibashi\\.net|basil\\.freemail\\.ne\\.jp|basketball\\.co\\.jp|batik\\.to|bay-watch\\.com|bayareaoffice\\.com|beenhad\\.com|beijingoffice\\.com|bel\\.ro|berlin\\.com|berlinoffice\\.com|bettiemail\\.com|bibleblack\\.net|bigfoot\\.com|bigtree\\.cx|bikerider\\.com|bishoujosenshi\\.com|black\\.livedoor\\.com|blackmail\\.cn|blademail\\.net|blazemail\\.com|blipmail\\.net|blowsearch\\.com|blue\\.livedoor\\.com|boardermail\\.com|boarderzone\\.com|bokumono\\.com|bolt\\.com|bostonoffice\\.com|bou\\.jp|bou\\.ne\\.jp|bowling\\.co\\.jp|boys\\.ro|boytype\\.com|boyzoneclub\\.com|brainpowerd\\.com|brassband\\.jp|brazil\\.co\\.jp|britneyclub\\.com|btamail\\.net\\.cn|btinternet\\.com|budweiser\\.com|buffbody\\.com|bughunt\\.net|bullsfan\\.com|bullsgame\\.com|bz-fan\\.net|cafesta\\.com|california\\.co\\.jp|canada\\.co\\.jp|canada\\.com|cancer\\.livedoor\\.com|candy-cafe\\.com|candy-pop\\.net|candypot\\.net|canwetalk\\.com|capella\\.freemail\\.ne\\.jp|capricorn\\.livedoor\\.com|cardblvd\\.com|carolinaoffice\\.com|casino\\.com|catholic\\.org|catlover\\.com|catnip\\.freemail\\.ne\\.jp|celineclub\\.com|cephiro\\.com|cess\\.ne\\.jp|chakume\\.ro|chan\\.co\\.jp|chan\\.fm|chan\\.jp|chan\\.ne\\.jp|chan\\.nu|chance\\.nu|chandi\\.to|cheerful\\.com|chemist\\.com|chetona\\.com|chi\\.ne\\.jp|chicagooffice\\.com|chin\\.jp|chin\\.ne\\.jp|chiyu\\.jp|cho-email\\.com|classified\\.co\\.jp|clerk\\.com|clickmail\\.ne\\.jp|cliffhanger\\.com|club\\.cx|club\\.li|club\\.ne\\.jp|clubbbq\\.com|clubs\\.co\\.jp|cm\\.cx|co2\\.jp|cock\\.livedoor\\.com|coco\\.co\\.jp|cocoa\\.freemail\\.ne\\.jp|coffin-rock\\.com|collegeclub\\.com|collegemail\\.com|colorado\\.co\\.jp|columnist\\.com|comeon\\.cx|comic-geek\\.com|comic\\.com|comicsfan\\.net|comomo\\.net|connecticut\\.co\\.jp|consultant\\.com|cornerpub\\.com|corporatedirtbag\\.com|corporation\\.jp|cosmos-21\\.com|counsellor\\.com|crazedanddazed\\.com|crazysexycool\\.com|crimsonguard\\.net|crystal-tokyo\\.com|csc\\.jp|curio-city\\.com|cute\\.ro|cuteandcuddly\\.com|cutey\\.com|cyberbabies\\.com|cybercafemaui\\.com|cyberloveplace\\.com|cyberspace\\.co\\.jp|cybertown\\.co\\.jp|d0c0m0\\.com|daisuke\\.zzn\\.com|dallasoffice\\.com|dangerous-minds\\.com|daredemo\\.com|dark-angel\\.jp|darkhorsefan\\.net|darkhorsemail\\.net|daruma\\.co\\.jp|dazedandconfused\\.com|dbzmail\\.com|dcemail\\.com|dcoffices\\.com|dddd\\.ne\\.jp|deal-maker\\.com|death-star\\.com|delaware\\.co\\.jp|deliveryman\\.com|deneb\\.freemail\\.ne\\.jp|denshobato\\.com|denveroffice\\.com|denwa\\.co\\.jp|design\\.co\\.jp|desu\\.jp|desu\\.ne\\.jp|devilhunter\\.com|dhmail\\.net|diamonds-mail\\.zzn\\.com|diplomats\\.com|dk-net\\.21\\.fm|dk-net\\.zzn\\.com|dknet-fk\\.zzn\\.com|dknet-marin\\.zzn\\.com|dknet-sky\\.zzn\\.com|dknet-sound\\.zzn\\.com|do-z\\.net|doctor\\.com|dog\\.cx|doglover\\.com|dogmail\\.co\\.uk|domainbank\\.cc|domeon\\.ro|domo39\\.com|dontmesswithtexas\\.com|doramail\\.com|dq\\.st|dr\\.com|dragon\\.livedoor\\.com|dragonslave\\.com|dreammail\\.ne\\.jp|drive\\.co\\.jp|dublin\\.com|e-garfield\\.com|e-otegami\\.net|e-techou\\.com|earth\\.co\\.jp|earth\\.livedoor\\.com|earthalliance\\.com|earthdome\\.com|earthling\\.net|eastmail\\.com|eath\\.co\\.jp|eigo\\.co\\.jp|elhazard\\.net|elrancho\\.com|elvis\\.com|email\\.com|email\\.it|emeralds\\.zzn\\.com|emojibin\\.jp|end-war\\.com|engineer\\.com|enkaibucho\\.net|esampo\\.ne\\.jp|estyle\\.ne\\.jp|europe\\.com|every-mail\\.com|excite\\.co\\.jp|execs\\.com|expert\\.ne\\.jp|ezwebmailer\\.com|f-1\\.ns\\.tc|facehugger\\.com|fact-mail\\.com|fan\\.cx|fanboy\\.org|fanboyz\\.com|fangirl\\.org|fangirlz\\.com|fastermail\\.com|financier\\.com|finebody\\.com|firemail\\.de|fish\\.co\\.jp|fishhoo\\.com|fishing\\.co\\.jp|flashmail\\.com|flora\\.ne\\.jp|floral\\.jp|florida\\.co\\.jp|fm\\.fullscoop\\.com|fmail\\.to|fooos\\.com|football\\.co\\.jp|for-president\\.com|forpresident\\.com|forum\\.cx|forum\\.jp|forum\\.ne\\.jp|free\\.japandesign\\.ne\\.jp|free\\.love3\\.net|freeandsingle\\.com|freecom\\.ne\\.jp|freejpn\\.com|freemail\\.ne\\.jp|freemailasia\\.com|freeserve\\.ne\\.jp|fresnomail\\.com|friend\\.cx|friendsfan\\.com|fubako\\.com|fuji-jp\\.com|fukuoka\\.fm|funifuni\\.net|furinkanhigh\\.com|fushigiyugi\\.com|fwd\\.revery\\.net|gackt\\.st|gadguard\\.net|gaijin\\.co\\.jp|galaxy5\\.com|galaxypolice\\.com|ganbo\\.net|gardener\\.com|gariya\\.com|garnets\\.zzn\\.com|garuda\\.to|gdnmail\\.net|ge999\\.com|geisha\\.zzn\\.com|gemini\\.livedoor\\.com|general-hospital\\.com|gensoutairiku\\.com|geocities\\.co\\.jp|geocities\\.com|geologist\\.com|georgia\\.co\\.jp|get\\.or\\.jp|ggpmail\\.net|ghostmail\\.net|giants-jp\\.com|gim\\.to|gingamail\\.net|ginko\\.co\\.jp|girlofyourdreams\\.com|givememail\\.net|givepeaceachance\\.com|glay-hisashi\\.com|glay-jiro\\.com|glay-takuro\\.com|glay-teru\\.com|glay\\.org|glaystyle\\.net|globetown\\.net|gmail\\.com|go2netmail\\.com|goddessmail\\.com|godhand\\.com|gofield\\.com|gogopop\\.net|gohip\\.com|gold\\.livedoor\\.com|goomail\\.com|gospeedgo\\.com|goto3\\.org|goukaku\\.com|gp\\.ro|gr\\.fm|graduatestudies\\.com|graphic-designer\\.com|grendelfan\\.com|grungecafe\\.com|gt\\.ro|gtr\\.nu|gundampilot\\.org|gunsmithcats\\.com|gunsmithcats\\.org|guntan\\.jp|guyofyourdreams\\.com|hage\\.zzn\\.com|hairdresser\\.net|hamal\\.freemail\\.ne\\.jp|hamasakiayumi\\.net|hamptonroads\\.com|hang-ten\\.com|hanko\\.co\\.jp|happiness\\.ath\\.cx|harlock\\.net|haruki-e\\.net|hasegawa3\\.net|hashimoto3\\.net|hawaiian\\.co\\.jp|hayashi3\\.org|heart\\.ro|heartthrob\\.com|heesun\\.net|hehe\\.com|hellboyfan\\.com|hello\\.co\\.jp|hellokitty\\.com|high-school\\.ro|hikaru\\.jp|hikoboshi\\.net|hikyaku\\.com|hingismartina\\.net|hirano3\\.net|hiroba\\.net|hollywood\\.co\\.jp|hollywoodkids\\.com|honki\\.net|horse\\.livedoor\\.com|hot-shot\\.com|hot\\.co\\.jp|hot\\.jp|hotmac\\.com|hotmail\\.co\\.jp|hotmail\\.com|hotpop3\\.com|hsuchi\\.net|hushmail\\.com|hutmail\\.net|hyper\\.cx|i-cf\\.com|i-get\\.ne\\.jp|i-name\\.org|iamgenki\\.com|iamit\\.com|iamwaiting\\.com|iamwasted\\.com|iamyours\\.com|iat\\.ne\\.jp|ichiban-shibori\\.com|id\\.nu|idaho\\.co\\.jp|ididitmyway\\.com|idomo\\.to|ie\\.cx|ihavepms\\.com|ii-park\\.net|iiwa\\.net|ijk\\.com|ijustdontcare\\.com|ikeda3\\.net|illinois\\.co\\.jp|illust\\.ro|illustrate\\.ro|ilovechocolate\\.com|iloveu-jp\\.com|imneverwrong\\.com|imstressed\\.com|imtoosexy\\.com|iname\\.com|india\\.com|indiana\\.co\\.jp|infospace\\.com|inorbit\\.com|inoue3\\.net|insurer\\.com|intaa\\.net|inter7\\.jp|internetdrive\\.com|iowa\\.co\\.jp|iscandar\\.com|isellcars\\.com|ishida3\\.net|ishihara3\\.net|ishikawa-rika\\.net|ishikawa3\\.net|isonly\\.net|isp\\.2ch\\.net|ito3\\.org|itookmyprozac\\.com|itpmail\\.itp\\.ne\\.jp|ivebeenframed\\.com|ivory\\.navi-a\\.com|ix\\.bz|j-wave\\.net|jam\\.ro|japan-jp\\.com|japan\\.co\\.jp|japan\\.com|japoness\\.com|jazzandjava\\.com|jazzgame\\.com|jesusanswers\\.com|jetin\\.net|jmail\\.co\\.jp|jobmail\\.to|jobs\\.co\\.jp|joinme\\.com|journalist\\.com|jpopmail\\.com|juno\\.com|jusenkyo\\.com|jweb\\.co\\.jp|kago-ai\\.com|kago\\.tv|kamei-eri\\.com|kanagawa\\.to|kanko\\.co\\.jp|kansas\\.co\\.jp|kao\\.li|kaori\\.com|karaage\\.net|karugaru\\.net|kato3\\.net|kawabata3\\.net|kayanarumi\\.com|kebi\\.com|keg-party\\.com|kellychen\\.com|kenbo\\.zzn\\.com|keromail\\.com|keyakiclub\\.net|kichimail\\.com|kigaru\\.com|kigaru\\.zzn\\.com|kikaku\\.ro|kimura3\\.net|king-postman\\.com|kingpulsar\\.ath\\.cx|kinki-kids\\.com|kirinji\\.zzn\\.com|kiss\\.sh|kittymail\\.com|kix\\.ne\\.jp|km169\\.net|knight-sabers\\.com|kobayashi3\\.net|kobe-city\\.com|kobej\\.zzn\\.com|kondo3\\.net|konno-asami\\.com|kornfreak\\.com|ktai\\.ro|ktplan\\.zzn\\.com|kukamail\\.com|kun\\.jp|kun\\.ne\\.jp|kurofune\\.co\\.jp|kyouin\\.com|laescuela\\.com|lajollashores\\.com|laoffices\\.com|laoficina\\.com|lasvegas\\.co\\.jp|launiversidad\\.com|lavie-mail\\.com|lawyer\\.com|le\\.to|leehom\\.net|legislator\\.com|leonlai\\.net|letsjam\\.com|letter-box\\.ath\\.cx|letter\\.or\\.jp|libra\\.livedoor\\.com|lily\\.freemail\\.ne\\.jp|lime\\.livedoor\\.com|linainverse\\.net|linomail\\.de|ll\\.to|llll\\.li|lobbyist\\.com|localbar\\.com|lodoss\\.org|london\\.com|londonoffice\\.com|lookingforme\\.com|lopezclub\\.com|louisiana\\.co\\.jp|louiskoo\\.com|love2\\.com|love2\\.ne\\.jp|love3\\.net|love7\\.com|loveable\\.com|loveboat\\.cx|loveletter\\.to|lovelys\\.jp|lovemailbox\\.com|lover-boy\\.com|lovergirl\\.com|loves-music\\.com|loveu\\.ro|lunashine\\.net|lycos\\.com|lycos\\.ne\\.jp|m00v\\.com|m98\\.ne\\.jp|macbox\\.com|macrosscity\\.com|macsrbetter\\.com|mad\\.scientist\\.com|madmanmail\\.com|madrid\\.com|maeda3\\.net|magicgirl\\.com|maido3\\.net|mail-box\\.jp|mail-center\\.com|mail-home\\.jp|mail-office\\.jp|mail\\.cn|mail\\.com|mail\\.goo\\.ne\\.jp|mail\\.infoseek\\.co\\.jp|mail\\.td|mail\\.yahoo\\.co\\.jp|mail2aaron\\.com|mail2abby\\.com|mail2alan\\.com|mail2alec\\.com|mail2alexa\\.com|mail2alicia\\.com|mail2allan\\.com|mail2allen\\.com|mail2allison\\.com|mail2alyssa\\.com|mail2amanda\\.com|mail2amber\\.com|mail2andrea\\.com|mail2andy\\.com|mail2angela\\.com|mail2ann\\.com|mail2anna\\.com|mail2anne\\.com|mail2anthony\\.com|mail2april\\.com|mail2arnold\\.com|mail2arthur\\.com|mail2ashley\\.com|mail2barbara\\.com|mail2becky\\.com|mail2ben\\.com|mail2bernard\\.com|mail2beth\\.com|mail2betty\\.com|mail2beverly\\.com|mail2billy\\.com|mail2blake\\.com|mail2bob\\.com|mail2bobby\\.com|mail2bradley\\.com|mail2brian\\.com|mail2brittany\\.com|mail2brook\\.com|mail2bruce\\.com|mail2bryan\\.com|mail2calvin\\.com|mail2caroline\\.com|mail2carolyn\\.com|mail2casey\\.com|mail2cathy\\.com|mail2charles\\.com|mail2christie\\.com|mail2christy\\.com|mail2chuck\\.com|mail2cindy\\.com|mail2clark\\.com|mail2claude\\.com|mail2cliff\\.com|mail2clint\\.com|mail2colin\\.com|mail2cory\\.com|mail2courtney\\.com|mail2craig\\.com|mail2crystal\\.com|mail2curt\\.com|mail2cynthia\\.com|mail2dale\\.com|mail2dan\\.com|mail2dana\\.com|mail2danielle\\.com|mail2danny\\.com|mail2darlene\\.com|mail2darren\\.com|mail2dave\\.com|mail2dawn\\.com|mail2deanna\\.com|mail2debbie\\.com|mail2debby\\.com|mail2denise\\.com|mail2dennis\\.com|mail2derek\\.com|mail2diana\\.com|mail2diane\\.com|mail2dillon\\.com|mail2dirk\\.com|mail2dominic\\.com|mail2don\\.com|mail2donald\\.com|mail2donna\\.com|mail2doris\\.com|mail2dorothy\\.com|mail2doug\\.com|mail2douglas\\.com|mail2dustin\\.com|mail2dylan\\.com|mail2earl\\.com|mail2eddie\\.com|mail2edgar\\.com|mail2edwin\\.com|mail2eli\\.com|mail2elizabeth\\.com|mail2ellen\\.com|mail2elliot\\.com|mail2emily\\.com|mail2eric\\.com|mail2erica\\.com|mail2erin\\.com|mail2ernie\\.com|mail2ethan\\.com|mail2eva\\.com|mail2evan\\.com|mail2evelyn\\.com|mail2florence\\.com|mail2floyd\\.com|mail2frank\\.com|mail2franklin\\.com|mail2fred\\.com|mail2freddie\\.com|mail2gabriel\\.com|mail2gail\\.com|mail2gary\\.com|mail2gavin\\.com|mail2gene\\.com|mail2george\\.com|mail2gerald\\.com|mail2gilbert\\.com|mail2gina\\.com|mail2glen\\.com|mail2gloria\\.com|mail2gordon\\.com|mail2grace\\.com|mail2graham\\.com|mail2grant\\.com|mail2greg\\.com|mail2guy\\.com|mail2hal\\.com|mail2hank\\.com|mail2hannah\\.com|mail2harold\\.com|mail2harry\\.com|mail2heather\\.com|mail2heidi\\.com|mail2helen\\.com|mail2henry\\.com|mail2herman\\.com|mail2holly\\.com|mail2homer\\.com|mail2howard\\.com|mail2hugh\\.com|mail2ian\\.com|mail2irene\\.com|mail2irving\\.com|mail2irwin\\.com|mail2isaac\\.com|mail2jackie\\.com|mail2jacob\\.com|mail2jaime\\.com|mail2jake\\.com|mail2james\\.com|mail2jamie\\.com|mail2jan\\.com|mail2jane\\.com|mail2janet\\.com|mail2janice\\.com|mail2jasmine\\.com|mail2jason\\.com|mail2jay\\.com|mail2jed\\.com|mail2jeffrey\\.com|mail2jennifer\\.com|mail2jenny\\.com|mail2jeremy\\.com|mail2jerry\\.com|mail2jessica\\.com|mail2jessie\\.com|mail2jim\\.com|mail2jimmy\\.com|mail2joan\\.com|mail2joann\\.com|mail2joanna\\.com|mail2jody\\.com|mail2joe\\.com|mail2joel\\.com|mail2joey\\.com|mail2john\\.com|mail2jon\\.com|mail2jonathan\\.com|mail2joseph\\.com|mail2josh\\.com|mail2juan\\.com|mail2judy\\.com|mail2julian\\.com|mail2julie\\.com|mail2justin\\.com|mail2karen\\.com|mail2karl\\.com|mail2kathleen\\.com|mail2kathy\\.com|mail2katie\\.com|mail2kay\\.com|mail2keith\\.com|mail2kelly\\.com|mail2kelsey\\.com|mail2ken\\.com|mail2kendall\\.com|mail2kenneth\\.com|mail2kenny\\.com|mail2kerry\\.com|mail2kevin\\.com|mail2kim\\.com|mail2kimberly\\.com|mail2kirk\\.com|mail2kristin\\.com|mail2kurt\\.com|mail2kyle\\.com|mail2lance\\.com|mail2larry\\.com|mail2laura\\.com|mail2lauren\\.com|mail2laurie\\.com|mail2lawrence\\.com|mail2lee\\.com|mail2leon\\.com|mail2leonard\\.com|mail2leone\\.com|mail2leslie\\.com|mail2linda\\.com|mail2lionel\\.com|mail2lisa\\.com|mail2liz\\.com|mail2lloyd\\.com|mail2lois\\.com|mail2lola\\.com|mail2lori\\.com|mail2lou\\.com|mail2louis\\.com|mail2lucy\\.com|mail2lyle\\.com|mail2lynn\\.com|mail2madison\\.com|mail2maggie\\.com|mail2mandy\\.com|mail2marc\\.com|mail2marcia\\.com|mail2margaret\\.com|mail2margie\\.com|mail2maria\\.com|mail2marilyn\\.com|mail2mark\\.com|mail2marries\\.com|mail2marsha\\.com|mail2martha\\.com|mail2martin\\.com|mail2marty\\.com|mail2marvin\\.com|mail2mary\\.com|mail2mason\\.com|mail2matt\\.com|mail2matthew\\.com|mail2maurice\\.com|mail2max\\.com|mail2maxwell\\.com|mail2megan\\.com|mail2mel\\.com|mail2melanie\\.com|mail2melissa\\.com|mail2melody\\.com|mail2michael\\.com|mail2michelle\\.com|mail2mike\\.com|mail2mildred\\.com|mail2milton\\.com|mail2mitch\\.com|mail2molly\\.com|mail2monica\\.com|mail2monty\\.com|mail2nancy\\.com|mail2nathan\\.com|mail2neal\\.com|mail2ned\\.com|mail2neil\\.com|mail2nelson\\.com|mail2nick\\.com|mail2nicole\\.com|mail2noah\\.com|mail2noel\\.com|mail2noelle\\.com|mail2norman\\.com|mail2oliver\\.com|mail2parker\\.com|mail2pat\\.com|mail2patricia\\.com|mail2patrick\\.com|mail2patty\\.com|mail2paul\\.com|mail2paula\\.com|mail2peggy\\.com|mail2perry\\.com|mail2pete\\.com|mail2peter\\.com|mail2phil\\.com|mail2phyllis\\.com|mail2rachel\\.com|mail2ralph\\.com|mail2randy\\.com|mail2ray\\.com|mail2raymond\\.com|mail2rebecca\\.com|mail2reed\\.com|mail2reggie\\.com|mail2rex\\.com|mail2richard\\.com|mail2ricky\\.com|mail2riley\\.com|mail2rita\\.com|mail2rob\\.com|mail2robert\\.com|mail2roberta\\.com|mail2robin\\.com|mail2rod\\.com|mail2rodney\\.com|mail2ron\\.com|mail2ronald\\.com|mail2ronnie\\.com|mail2rosie\\.com|mail2roy\\.com|mail2rudy\\.com|mail2russell\\.com|mail2rusty\\.com|mail2ruth\\.com|mail2ryan\\.com|mail2sabrina\\.com|mail2sal\\.com|mail2sam\\.com|mail2samantha\\.com|mail2sandra\\.com|mail2sandy\\.com|mail2sara\\.com|mail2sarah\\.com|mail2scott\\.com|mail2sean\\.com|mail2seth\\.com|mail2shane\\.com|mail2sharon\\.com|mail2shawn\\.com|mail2shirley\\.com|mail2simon\\.com|mail2site\\.com|mail2stacy\\.com|mail2stan\\.com|mail2stanley\\.com|mail2stephanie\\.com|mail2steve\\.com|mail2steven\\.com|mail2stewart\\.com|mail2susan\\.com|mail2suzie\\.com|mail2sylvia\\.com|mail2tammy\\.com|mail2tanya\\.com|mail2tara\\.com|mail2taylor\\.com|mail2ted\\.com|mail2terri\\.com|mail2terry\\.com|mail2tiffany\\.com|mail2tim\\.com|mail2timothy\\.com|mail2tina\\.com|mail2toby\\.com|mail2todd\\.com|mail2tom\\.com|mail2tommy\\.com|mail2tony\\.com|mail2tracey\\.com|mail2tracy\\.com|mail2travis\\.com|mail2troy\\.com|mail2tyler\\.com|mail2valerie\\.com|mail2vanessa\\.com|mail2vickie\\.com|mail2victor\\.com|mail2victoria\\.com|mail2vince\\.com|mail2wally\\.com|mail2walter\\.com|mail2warren\\.com|mail2wayne\\.com|mail2wendell\\.com|mail2wendy\\.com|mail2whitney\\.com|mail2wilbur\\.com|mail2willard\\.com|mail2willie\\.com|mail2zack\\.com|mail7\\.jp|mail7\\.ph|mailandnews\\.com\\.hk|mailbank\\.ne\\.jp|mailcenter\\.cc|mailclub-dknet\\.zzn\\.com|mailexcite\\.com|mailfriend\\.net|mailhost\\.net|mailmagic\\.org|maine\\.co\\.jp|mamegohan\\.net|manbow\\.com|mangablast\\.com|mangamail\\.net|mangatown\\.com|marinenet\\.jp|married-not\\.com|mars\\.livedoor\\.com|marsattack\\.com|marvmail\\.com|maryland\\.co\\.jp|masakishrine\\.com|mattete\\.net|matumoto3\\.net|mauimail\\.com|max\\.sh|mcn\\.ne\\.jp|mechpilot\\.com|megatokyo\\.org|meishi\\.co\\.jp|melmel\\.tv|melu\\.jp|memory\\.jp|mercury\\.livedoor\\.com|meritmail\\.net|mexico\\.co\\.jp|mi\\.to|mibarrio\\.com|michigan\\.co\\.jp|michishige-sayumi\\.com|mifinca\\.com|mihacienda\\.com|miho-nakayama\\.com|milk\\.freemail\\.ne\\.jp|millionaireintraining\\.com|millto\\.net|mindless\\.com|minister\\.com|mint\\.freemail\\.ne\\.jp|mippi-mail\\.com|missouri\\.co\\.jp|miyahara3\\.net|mizar\\.freemail\\.ne\\.jp|mizuho\\.zzn\\.com|ml-member\\.com|mmu\\.jp|mo-om\\.net|mocha\\.freemail\\.ne\\.jp|moco\\.ne\\.jp|momo-mail\\.com|mon8\\.net|monkey\\.livedoor\\.com|montana\\.co\\.jp|moon\\.co\\.jp|moon\\.mu|moon\\.sh|moonkingdom\\.com|moonlady\\.net|moonman\\.com|moonshinehollow\\.com|moonstones\\.zzn\\.com|mori3\\.net|morningmusume\\.com|moscowmail\\.com|moscowoffice\\.com|most-wanted\\.com|mostlysunny\\.com|mouse\\.livedoor\\.com|mr-potatohead\\.com|msg-reader\\.com|mukae\\.com|munich\\.com|musician\\.org|mxxm\\.net|my-name\\.org|mybook\\.to|mycabin\\.com|mycampus\\.com|mydotcomaddress\\.com|myestate\\.com|myflat\\.com|mylaptop\\.com|myna\\.jp|mynetaddress\\.com|myownemail\\.com|mypad\\.com|mypost\\.jp|myself\\.com|mystupidjob\\.com|mystupidschool\\.com|nabebugyo\\.net|nakagawa3\\.net|nakajima3\\.net|nakamura3\\.org|nakayoshi\\.cc|nandeda\\.ro|naplesnews\\.net|nativestar\\.net|naver\\.ne\\.jp|navy\\.livedoor\\.com|nctta\\.org|ne\\.nu|nebraska\\.co\\.jp|nekonohige\\.net|neo-tokyo\\.org|neptune\\.livedoor\\.com|nergal\\.org|nervhq\\.org|net\\.atn\\.ne\\.jp|netbig\\.com|netexecutive\\.com|netexpressway\\.com|netidol\\.jp|netlane\\.com|netlimit\\.com|netspeedway\\.com|netzone\\.cc|nevada\\.co\\.jp|newmexico\\.co\\.jp|newyork\\.co\\.jp|newyorkoffice\\.com|nic-asia\\.net|nic-asia\\.org|nicegal\\.com|nicholastse\\.net|nicolastse\\.com|nihongo\\.co\\.jp|nirvanafan\\.com|norika-fujiwara\\.com|norikomail\\.com|northcarolina\\.co\\.jp|northdakota\\.co\\.jp|notme\\.com|nyanmail\\.com|nycmail\\.com|o-gata\\.com|o-tay\\.com|of\\.ai|office\\.cx|office\\.ne\\.jp|office\\.to|official-site\\.net|ogawa3\\.net|ohio-state\\.com|ohio\\.co\\.jp|ohiooffice\\.com|ohno3\\.net|ohtoriacademy\\.com|oka-3\\.net|okada3\\.net|okamoto3\\.net|oklahoma\\.co\\.jp|olive\\.freemail\\.ne\\.jp|olive\\.td|olmail\\.to|one-3\\.net|onecooldude\\.com|oni-3\\.net|onion\\.soup\\.jp|online-member\\.com|operamail\\.com|optician\\.com|or\\.nu|oregon\\.co\\.jp|orihime\\.net|osusume\\.org|otakumail\\.com|otegami\\.com|oto-3\\.net|otokorashii\\.com|over-the-rainbow\\.com|packersfan\\.com|paris\\.com|parisoffice\\.com|parkjiyoon\\.com|parlor\\.ath\\.cx|partlycloudy\\.com|passage\\.ne\\.jp|patipati\\.net|pc_run\\.zzn\\.com|pcpostal\\.com|peaceful\\.ath\\.cx|pediatrician\\.com|pennoffice\\.com|pennsylvania\\.co\\.jp|penpen\\.com|peridot\\.zzn\\.com|peru\\.co\\.jp|pets-mail\\.com|phayze\\.com|phoenixoffice\\.com|phone\\.co\\.jp|photohighway\\.co\\.jp|pink\\.livedoor\\.com|pinkrabbit\\.zzn\\.com|pisces\\.livedoor\\.com|piyodamari\\.net|piyomail\\.com|pizza\\.co\\.jp|place\\.ath\\.cx|place\\.co\\.jp|planetjurai\\.com|planetnamek\\.org|playful\\.com|plum\\.freemail\\.ne\\.jp|pmail\\.ne\\.jp|pobox\\.org\\.sg|poetic\\.com|poohmail\\.jp|pool-sharks\\.com|pop-cute\\.net|popj\\.com|popmail\\.com|poporo\\.net|popstar\\.com|portlandoffice\\.com|positive-thinking\\.com|post-pe\\.to|post-pet\\.st|post\\.com|post\\.html\\.ne\\.jp|post1\\.com|postpet\\.ddo\\.jp|present\\.ro|presentget\\.net|presidency\\.com|press\\.co\\.jp|priest\\.com|prince\\.co\\.jp|princess\\.co\\.jp|pro\\.nu|programmer\\.net|project-2501\\.com|prontomail\\.com|proxymail\\.jp|psicorps\\.com|pt\\.tokainavi\\.ne\\.jp|pub\\.to|publicist\\.com|puchinet\\.com|pulp-fiction\\.com|pyon\\.jp|pyon\\.ne\\.jp|quackquack\\.com|realtyagent\\.com|rebelguard\\.com|rebelspy\\.net|recycler\\.com|redeyemail\\.com|rednecks\\.com|refreshmail\\.com|registerednurses\\.com|reiko\\.nu|repairman\\.com|representative\\.com|rescueteam\\.com|revery-earth\\.com|rgm-79\\.com|rickymail\\.com|rinn\\.jp|rinn\\.ne\\.jp|rocketcomics\\.net|rocketmail\\.com|rodrun\\.com|roguemail\\.net|rome\\.com|root99\\.com|rose\\.freemail\\.ne\\.jp|rubyridge\\.com|rubys-mail\\.zzn\\.com|rurouni\\.com|sabermail\\.com|sacbeemail\\.com|safe-mail\\.ne\\.jp|sailormoon\\.com|sailormoonfan\\.com|sailorsenshi\\.com|saintly\\.com|saito3\\.net|saiyan\\.com|sak2\\.com|sakata3\\.net|saku2\\.com|samerica\\.com|sammimail\\.com|samuraideeperkyo\\.net|sandiegooffice\\.com|sanfranmail\\.com|sann\\.jp|sann\\.ne\\.jp|sao\\.li|sapphire-mail\\.zzn\\.com|sasaki3\\.net|sasebo\\.net|sato\\.kz|sato3\\.net|schoolemail\\.com|scientist\\.com|scifianime\\.com|scififan\\.com|scorpius\\.livedoor\\.com|sdf-1\\.com|seattleoffice\\.com|secondimpact\\.com|section-9\\.com|section2\\.com|seductive\\.com|sell\\.co\\.jp|sendme-mail\\.net|seroji\\.com|seroji\\.net|seroji\\.org|sesmail\\.com|seventeen\\.st|shadowlady\\.com|shaniastuff\\.com|sharonapple\\.com|sheep\\.livedoor\\.com|shes\\.net|shibuya109\\.com|shimaguni\\.com|shimbun\\.co\\.jp|shimizu3\\.net|shinra\\.org|shop-member\\.com|siammail\\.com|silentmail\\.net|silver\\.li|silver\\.livedoor\\.com|sina\\.com|singapore\\.com|singor\\.net|slipknot\\.jp|smapxsmap\\.net|smashing-pumpkins\\.com|smileyface\\.com|smoug\\.net|snake\\.livedoor\\.com|so\\.ai|soccer\\.co\\.jp|sociologist\\.com|sohu\\.com|somethingorother\\.com|soon\\.com|soundonsound\\.com|southcarolina\\.co\\.jp|southdakota\\.co\\.jp|soyokaze\\.org|spacebattleship\\.com|spacemail\\.com|speed-racer\\.com|spica\\.freemail\\.ne\\.jp|spl\\.at|sport\\.co\\.jp|sportsaddict\\.com|sportsman\\.co\\.jp|spyboymail\\.net|spyring\\.com|squaresoftrules\\.com|stage\\.st|starmail\\.com|starplace\\.com|stopdropandroll\\.com|success\\.ath\\.cx|suke\\.jp|suke\\.ne\\.jp|sunrise-sunset\\.com|sunsgame\\.com|superdeformed\\.com|supernetpower\\.com|surfy\\.net|sushi\\.co\\.jp|suzuki\\.sh|suzuki3\\.net|suzukiemi\\.com|sweetlovemail\\.com|sydneyoffice\\.com|system\\.li|tackey\\.net|taka\\.be|takahashi\\.ai|takahashi\\.sh|takahashi3\\.net|takahashiai\\.net|takao3\\.net|takasu\\.or\\.jp|takechiyo\\.net|takeshifan\\.com|takuyakimura\\.com|tamageta\\.net|tanaka-reina\\.com|tanaka3\\.net|tani3\\.net|tankpolice\\.com|taurus\\.livedoor\\.com|teacher\\.com|team-rocket\\.net|teamgear\\.net|techie\\.com|technologist\\.com|teddybear\\.to|teenagedirtbag\\.com|tekmail\\.com|telekbird\\.com\\.cn|tellmeimcute\\.com|tendodojo\\.com|tennessee\\.co\\.jp|terebi\\.co\\.jp|texas\\.co\\.jp|texhnolyze\\.com|tfaw\\.net|the-any-key\\.com|the-big-apple\\.com|the-eagles\\.com|the-lair\\.com|the-pentagon\\.com|the-police\\.com|the-stock-market\\.com|the18th\\.com|theairforce\\.com|thearmy\\.com|thebeachpad\\.com|thecoastguard\\.com|thedoghousemail\\.com|thedorm\\.com|thegolfcourse\\.com|theheadoffice\\.com|thehelm\\.com|thekeyboard\\.com|thelanddownunder\\.com|themail\\.com|themarines\\.com|thenavy\\.com|theraces\\.com|theracetrack\\.com|theteebox\\.com|thevortex\\.com|tiger\\.livedoor\\.com|tigerdrive\\.com|tigers-jp\\.com|to-sen\\.net|tok2\\.com|tokinodaichi\\.com|tokutoku\\.or\\.jp|tokyo-3\\.com|tokyo-underground\\.net|tokyo\\.com|tokyo\\.jt7\\.net|tokyo24\\.com|toosexyforyou\\.com|topazs\\.zzn\\.com|tora\\.zzn\\.com|tourmaline\\.zzn\\.com|tousen\\.com|toys\\.co\\.jp|tramonline\\.net|triaez\\.com|tropicalstorm\\.com|trust-me\\.com|turquois\\.zzn\\.com|twincitiesoffice\\.com|u2club\\.com|uchi\\.co\\.jp|ucsd\\.com|ueda3\\.net|uetoaya\\.com|ultrapostman\\.com|umpire\\.com|underwriters\\.com|uno\\.ne\\.jp|unspacy\\.org|usa\\.co\\.jp|usa\\.com|usa\\.net|usagimail\\.com|utah\\.co\\.jp|utena\\.org|uymail\\.com|v7\\.ro|v99v\\.net|vampirehunter\\.com|vampirehunter\\.org|vanilla\\.freemail\\.ne\\.jp|venture\\.co\\.jp|veritechpilot\\.com|vermont\\.co\\.jp|violet\\.livedoor\\.com|vip\\.co\\.jp|vip777\\.fam\\.cx|virginia\\.co\\.jp|virgo\\.livedoor\\.com|virtual-mail\\.com|virtualmail\\.com|vivajpn\\.com|vivi\\.to|vivianhsu\\.net|vo-ov\\.net|voo\\.to|vorlonempire\\.com|vui\\.cc|vxxv\\.net|w-inds\\.ws|wada3\\.net|waiting\\.ath\\.cx|wakamatu\\.com|walkerplus\\.com|washington\\.co\\.jp|washingtondc\\.co\\.jp|watanabe3\\.net|wayan\\.to|webave\\.com|webjetters\\.com|website\\.co\\.jp|wedgemail\\.com|westvirginia\\.co\\.jp|wetwetwet\\.com|white-star\\.com|whoever\\.com|wind-ensemble\\.jp|winning\\.com|winningteam\\.com|wisconsin\\.co\\.jp|wish\\.tk|witty\\.com|wiz98ms\\.zive\\.net|wolf-web\\.com|wonder\\.ne\\.jp|wongfaye\\.com|wouldilie\\.com|writeme\\.com|wt\\.ro|wwdx\\.net|www2\\.to|www3\\.to|wyoming\\.co\\.jp|x21\\.net|xbox\\.com|xfilesfan\\.com|xmail\\.to|xmas\\.li|yabumi\\.com|yabumi\\.jp|yada-yada\\.com|yagi\\.net|yaguchi-mari\\.com|yahoo\\.ca|yahoo\\.com|yahoo\\.com\\.ar|yahoo\\.com\\.au|yahoo\\.com\\.br|yahoo\\.com\\.cn|yahoo\\.com\\.hk|yahoo\\.com\\.in|yahoo\\.com\\.kr|yahoo\\.com\\.mx|yahoo\\.com\\.nz|yahoo\\.com\\.sg|yahoo\\.com\\.uk|yahoo\\.de|yahoo\\.dk|yahoo\\.es|yahoo\\.fr|yahoo\\.gr|yahoo\\.it|yahoo\\.no|yahoo\\.se|yamada3\\.net|yamaguchi3\\.net|yamamoto3\\.net|yamazaki3\\.net|yan\\.jp|yan\\.ne\\.jp|yasui3\\.net|yeayea\\.com|yokozuna\\.co\\.jp|yoshida3\\.net|yoshizawa-hitomi\\.com|youareadork\\.com|youngpostman\\.net|your-house\\.com|yours\\.com|yoyaku\\.st|yubin\\.co\\.jp|yumail\\.com|yume-21\\.com|yume\\.otegami\\.com|yumetairiku\\.net|yuppieintraining\\.com|yyhmail\\.com|zahadum\\.com|zaque\\.com|zdnetmail\\.ne\\.jp|zenno\\.biz|zenno\\.cn|zenno\\.jp|zentradei\\.com|zetima\\.net|zhaowei\\.net|uu\\.tok2\\.com|yesyes\\.jp|smoug\\.net|dynamic\\.163data\\.com\\.cn|ctinets\\.com|hkcable\\.com\\.hk|netvigator\\.com|dynamic\\.smartone-vodafone\\.com|tinp\\.apol\\.com\\.tw|dynamic\\.hinet\\.net|dynamic\\.kbronet\\.com\\.tw|dynamic\\.giga\\.net\\.tw|adsl\\.dynamic\\.seed\\.net\\.tw|dynamic\\.so-net\\.net\\.tw|static\\.tcol\\.com\\.tw|dynamic\\.tfn\\.net\\.tw|ipt\\.aol\\.com|biz\\.snlo\\.arrival\\.net|ameritech\\.net|bellsouth\\.net|ctb-mt\\.client\\.bresnan\\.net|buckeyecom\\.net|cpe\\.cableone\\.net|centurytel\\.net|charter\\.com|comcast\\.net|Illinois\\.hfc\\.comcastbusiness\\.net|cortland\\.com|sttnwaho\\.covad\\.net|cox\\.net|cvip\\.net|static\\.dejazzd\\.com|dslextreme\\.com|cable\\.mindspring\\.com|embarqhsd\\.net|dsl1\\.ekgv\\.ca\\.frontiernet\\.net|uds\\.hawaiiantel\\.net|ptreyes\\.horizoncable\\.com|jck\\.clearwire-dns\\.net|myactv\\.net|onelinkpr\\.net|dyn\\.optonline\\.net|lsan\\.mdsg-pacwest\\.com|res-cmts\\.sth\\.ptd\\.net|qwest\\.net|cfl\\.res\\.rr\\.com|pools\\.spcsdns\\.net|dh\\.suddenlink\\.net|ftth\\.swbr\\.surewest\\.net|verizon\\.net|myvzw\\.com|cust\\.wildblue\\.net|dynamic\\.ip\\.windstream\\.net|nap\\.wideopenwest\\.com|sub\\.mbb\\.three\\.co\\.uk|bethere\\.co\\.uk|bb\\.sky\\.com|th\\.ifl\\.net|blueyonder\\.co\\.uk|red\\.bezeqint\\.net|fastwebnet\\.it|retail\\.telecomitalia\\.it|clienti\\.tiscali\\.it|speedy\\.telkom\\.net\\.id|shenwell\\.net|vic\\.bigpond\\.net\\.au|dyn\\.iinet\\.net\\.au|optusnet\\.com\\.au|lnk\\.telstra\\.net|tpgi\\.com\\.au|adsl\\.highway\\.telekom\\.at|adsl\\.xs4all\\.nl|maast1\\.lb\\.home\\.nl|dyn\\.aci\\.on\\.ca|b2b2c\\.ca|bell\\.ca|cia\\.com|cgocable\\.net|tor\\.pppoe\\.execulink\\.com|net\\.cable\\.rogers\\.com|yktn\\.hsdb\\.sasknet\\.sk\\.ca|shawcable\\.net|dsl\\.teksavvy\\.com|telus\\.net|mc\\.videotron\\.ca|maxonline\\.com\\.sg|cust\\.bluewin\\.ch|dclient\\.hispeed\\.ch|bredband\\.comhem\\.se|telia\\.com|dynamic\\.jazztel\\.es|dynamic\\.3bb\\.co\\.th|csloxinfo\\.net|adsl\\.dynamic\\.totbb\\.net|revip2\\.asianet\\.co\\.th|bk6-dsl\\.surnet\\.cl|cm\\.vtr\\.net|static\\.sonofon\\.dk|dynamic\\.dsl\\.tele\\.dk|pool\\.einsundeins\\.de|adsl\\.alicedsl\\.de|dip\\.t-dialin\\.net|dig-prov\\.de|dynamic\\.mnet-online\\.de|netcologne\\.de|pool\\.mediaWays\\.net|web\\.vodafone\\.de|plain\\.net\\.nz|jetstream\\.xtra\\.co\\.nz|dsl\\.dyn\\.ihug\\.co\\.nz|pool\\.t-online\\.hu|prtelecom\\.hu|pldt\\.net|gw\\.smartbro\\.net|bb\\.dnainternet\\.fi|elisa-laajakaista\\.fi|static\\.ssp\\.fi|rev\\.libertysurf\\.net|fbx\\.proxad\\.net|abo\\.wanadoo\\.fr|brutele\\.be|broadband\\.ctm\\.net|jaring\\.my|tm\\.net\\.my|dsl\\.orel\\.ru|broadband\\.corbina\\.ru|domolink\\.tula\\.net|dynvpn\\.flex\\.ru|pppoe\\.mtu-net\\.ru|pskovline\\.ru|qwerty\\.ru|dynamic\\.ufanet\\.ru|vidnoe\\.net|pppoe\\.mtu-net\\.ru|m9com\\.rumail\\.goo\\.ne\\.jp|mail\\.infoseek\\.co\\.jp|yahoo\\.co\\.jp|live\\.jp))".r;

  val RESULTS_PER_PAGE = 100
  
  val PLAN_MICRO = "micro"
  val PLAN_STARTER = "starter"
  val PLAN_BUSINESS = "business"
  val PLAN_UNLIMITED = "unlimited"
  
  val PLAN_MAP = Map(
    PLAN_MICRO -> PlanBean("Micro", 5, 5, 5, 5, 0),
    PLAN_STARTER -> PlanBean("Starter", 20, 20, 20, 20, 1050),
    PLAN_BUSINESS -> PlanBean("Business", 50, 50, 50, 50, 2100),
    PLAN_UNLIMITED -> PlanBean("Unlimited", -1, -1, -1, -1, -1))



  private def configPath: String = {
    try {
      CONFIG_FOLDER + File.separator + CONFIG_FILE
    } catch {
      case e: FileNotFoundException => "."
    }
  }

  private val DEFAULT_TIME_ZONE: TimeZone = TimeZone.getTimeZone("Asia/Tokyo")
  def timeZone: TimeZone = DEFAULT_TIME_ZONE;

  def dayCountFormat: DateFormat = {
    val dateFormat: DateFormat = new SimpleDateFormat("yyyyMMdd")
    dateFormat.setTimeZone(AppConstants.SYSTEM_TIME_ZONE)
    dateFormat
  }

  def dayCountFormatWithTimeZone(timeZone: TimeZone): DateFormat = {
    val dateFormat: DateFormat = new SimpleDateFormat("yyyyMMdd")
    dateFormat.setTimeZone(timeZone)
    dateFormat
  }

  def timeFormat: DateFormat = {
    val dateFormat: DateFormat = new SimpleDateFormat("HH:mm")
    dateFormat.setTimeZone(AppConstants.timeZone)
    dateFormat
  }

  def dateTimeFormat: DateFormat = {
    val dateFormat: DateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm")
    dateFormat.setTimeZone(AppConstants.timeZone)
    dateFormat
  }

  def dateFormat: DateFormat = {
    val dateFormat: DateFormat = new SimpleDateFormat("yyyy/MM/dd")
    dateFormat.setTimeZone(AppConstants.timeZone)
    dateFormat
  }

}
