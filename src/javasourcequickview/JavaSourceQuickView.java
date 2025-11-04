package javasourcequickview;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.text.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.regex.*;

public class JavaSourceQuickView extends JFrame {

    private static final String APP_TITLE = "Java Source Quick View";
    private static final double UI_SCALE = determineUiScale();
    private static final int BASE_FONT_SIZE = 26;
    private static final Color WINDOW_BACKGROUND = new Color(0xf4f6fb);
    private static final Color TOOLBAR_BACKGROUND = new Color(0xf9fafc);
    private static final Color SURFACE_COLOR = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(0xc1cad7);
    private static final Color ACCENT_COLOR = new Color(0x3367d6);
    private static final Color ACCENT_DARK = new Color(0x1a3e85);
    private static final Color BUTTON_HOVER = new Color(0x3f74f5);
    private static final Color BUTTON_PRESSED = new Color(0x274c9f);
    private static final Color BUTTON_DISABLED = new Color(0xaebbe6);
    private static final Color TREE_SELECTION_BG = new Color(0xdfe7ff);
    private static final Color TREE_SELECTION_BORDER = new Color(0xa5b9ff);
    private static final Color TREE_SELECTION_TEXT = new Color(0x1d335c);
    private static final Color TEXT_MUTED = new Color(0x4a4f57);
    private static final Highlighter.HighlightPainter DOC_HOVER_PAINTER =
        new UnderlineHighlightPainter(new Color(0x3367d6));
    private static final String VIET_SAMPLE_TEXT = "Số lượng ký tự tiếng Việt: ăâêôơưđ";
    private static final Font UI_FONT = createUIFont();
    private static final Font MONO_FONT = createMonoFont();
    private static final Set<String> CONTROL_KEYWORDS = new HashSet<>(Arrays.asList(
            "if", "for", "while", "switch", "catch", "return", "new", "throw", "assert", "synchronized"
    ));
    private static final Set<String> CHAR_SEQUENCE_METHODS = new HashSet<>(Arrays.asList(
            "length", "charat", "substring", "subsequence", "equals", "equalsignorecase", "compareto",
            "comparetoignorecase", "contains", "indexof", "lastindexof", "isempty", "trim", "replace",
            "replaceall", "replacefirst", "split", "touppercase", "tolowercase", "join", "format", "matches",
            "regionmatches", "concat", "tochararray", "intern", "getbytes", "codepointat", "codepointbefore",
            "codepointcount", "repeat", "lines", "strip", "stripleading", "striptrailing", "valueof"
    ));
    private static final String GENERIC_SUMMARY = "Thực hiện nghiệp vụ do bạn định nghĩa trong thân phương thức.";
    private static final Map<String, Map<String, String>> BUILTIN_DOCS = new HashMap<>();
    private static final Map<String, String> OWNER_ALIASES = new HashMap<>();

    static {
        // Owner aliases
        aliasOwner("list", "list");
        aliasOwner("arraylist", "list");
        aliasOwner("linkedlist", "list");
        aliasOwner("vector", "list");
        aliasOwner("stack", "stack");
        aliasOwner("set", "set");
        aliasOwner("hashset", "set");
        aliasOwner("linkedhashset", "set");
        aliasOwner("treeset", "set");
        aliasOwner("queue", "queue");
        aliasOwner("deque", "queue");
        aliasOwner("arraydeque", "queue");
        aliasOwner("priorityqueue", "queue");
        aliasOwner("map", "map");
        aliasOwner("hashmap", "map");
        aliasOwner("linkedhashmap", "map");
        aliasOwner("treemap", "map");
        aliasOwner("concurrenthashmap", "map");
        aliasOwner("entry", "map.entry");
        aliasOwner("map.entry", "map.entry");
        aliasOwner("string", "string");
        aliasOwner("stringbuilder", "stringbuilder");
        aliasOwner("stringbuffer", "stringbuilder");
        aliasOwner("charsequence", "charsequence");
        aliasOwner("stringjoiner", "stringjoiner");
        aliasOwner("character", "character");
        aliasOwner("integer", "integer");
        aliasOwner("double", "double");
        aliasOwner("random", "random");
        aliasOwner("exception", "exception");
        aliasOwner("localdate", "localdate");
        aliasOwner("localdatetime", "localdatetime");
        aliasOwner("localtime", "localtime");
        aliasOwner("instant", "instant");
        aliasOwner("duration", "duration");
        aliasOwner("optional", "optional");
        aliasOwner("arrays", "arrays");
        aliasOwner("collections", "collections");
        aliasOwner("collection", "collection");
        aliasOwner("collectors", "collectors");
        aliasOwner("stream", "stream");
        aliasOwner("intstream", "stream");
        aliasOwner("longstream", "stream");
        aliasOwner("doublestream", "stream");
        aliasOwner("predicate", "predicate");
        aliasOwner("function", "function");
        aliasOwner("consumer", "consumer");
        aliasOwner("supplier", "supplier");
        aliasOwner("binaryoperator", "binaryoperator");
        aliasOwner("unaryoperator", "unaryoperator");
        aliasOwner("math", "math");
        aliasOwner("system", "system");
        aliasOwner("printstream", "printstream");
        aliasOwner("printwriter", "printstream");
        aliasOwner("scanner", "scanner");
        aliasOwner("bufferedreader", "reader");
        aliasOwner("bufferedwriter", "writer");
        aliasOwner("filereader", "reader");
        aliasOwner("stringreader", "reader");
        aliasOwner("inputstreamreader", "reader");
        aliasOwner("filewriter", "writer");
        aliasOwner("stringwriter", "writer");
        aliasOwner("outputstreamwriter", "writer");
        aliasOwner("fileinputstream", "inputstream");
        aliasOwner("bytearrayinputstream", "inputstream");
        aliasOwner("objectinputstream", "inputstream");
        aliasOwner("fileoutputstream", "outputstream");
        aliasOwner("bytearrayoutputstream", "outputstream");
        aliasOwner("objectoutputstream", "outputstream");
        aliasOwner("array", "array");
        aliasOwner("object", "object");
        aliasOwner("objects", "objects");
        aliasOwner("thread", "thread");
        aliasOwner("runnable", "runnable");
        aliasOwner("callable", "callable");
        aliasOwner("iterator", "iterator");
        aliasOwner("iterable", "iterable");
        aliasOwner("comparator", "comparator");
        aliasOwner("comparable", "comparable");
        aliasOwner("file", "file");
        aliasOwner("path", "path");
        aliasOwner("paths", "paths");
        aliasOwner("files", "files");
        aliasOwner("inputstream", "inputstream");
        aliasOwner("outputstream", "outputstream");
        aliasOwner("reader", "reader");
        aliasOwner("writer", "writer");
        aliasOwner("objects.require", "objects");

        // String APIs
        registerDoc("string", "length", "Trả về số lượng ký tự của chuỗi hiện tại.");
        registerDoc("string", "isEmpty", "Kiểm tra chuỗi có rỗng (length == 0) hay không.");
        registerDoc("string", "substring", "Lấy chuỗi con từ vị trí bắt đầu (inclusive) tới vị trí kết thúc (exclusive).");
        registerDoc("string", "charAt", "Trả về ký tự tại chỉ số được chỉ định (0-based).");
        registerDoc("string", "subSequence", "Trả về CharSequence con trong khoảng begin-end (giống substring).");
        registerDoc("string", "indexOf", "Tìm vị trí xuất hiện đầu tiên của chuỗi/ ký tự con; trả về -1 nếu không thấy.");
        registerDoc("string", "lastIndexOf", "Tìm vị trí xuất hiện cuối cùng của chuỗi/ ký tự con.");
        registerDoc("string", "contains", "Kiểm tra chuỗi có chứa chuỗi con chỉ định hay không.");
        registerDoc("string", "startsWith", "Kiểm tra chuỗi có bắt đầu bằng tiền tố cho trước.");
        registerDoc("string", "endsWith", "Kiểm tra chuỗi có kết thúc bằng hậu tố cho trước.");
        registerDoc("string", "split", "Tách chuỗi thành mảng dựa trên biểu thức chính quy truyền vào; có thể truyền tham số giới hạn.");
        registerDoc("string", "replace", "Thay thế toàn bộ chuỗi con bằng chuỗi mới theo dạng văn bản thông thường.");
        registerDoc("string", "replaceAll", "Thay thế các kết quả khớp với biểu thức chính quy bằng chuỗi mới.");
        registerDoc("string", "replaceFirst", "Thay thế lần xuất hiện đầu tiên khớp với biểu thức chính quy.");
        registerDoc("string", "matches", "Kiểm tra toàn bộ chuỗi có khớp với regex cho trước hay không.");
        registerDoc("string", "regionMatches", "So sánh hai vùng con của chuỗi (có thể bỏ qua hoa/thường).");
        registerDoc("string", "concat", "Nối thêm chuỗi vào cuối, tương đương toán tử +.");
        registerDoc("string", "equals", "So sánh nội dung hai chuỗi theo từng ký tự (phân biệt hoa thường).");
        registerDoc("string", "equalsIgnoreCase", "So sánh hai chuỗi mà không phân biệt chữ hoa chữ thường.");
        registerDoc("string", "format", "Tạo chuỗi theo mẫu định dạng (giống printf) với các tham số đi kèm.");
        registerDoc("string", "join", "Ghép các phần tử Iterable/array thành một chuỗi với ký tự phân tách.");
        registerDoc("string", "repeat", "Lặp chuỗi hiện tại n lần và ghép lại (Java 11+).");
        registerDoc("string", "strip", "Loại bỏ khoảng trắng đầu cuối (Unicode aware).");
        registerDoc("string", "stripLeading", "Loại bỏ khoảng trắng ở đầu chuỗi (Java 11+).");
        registerDoc("string", "stripTrailing", "Loại bỏ khoảng trắng ở cuối chuỗi (Java 11+).");
        registerDoc("string", "toUpperCase", "Chuyển toàn bộ chuỗi sang chữ in hoa.");
        registerDoc("string", "toLowerCase", "Chuyển toàn bộ chuỗi sang chữ thường.");
        registerDoc("string", "valueOf", "Chuyển đổi giá trị bất kỳ sang biểu diễn chuỗi.");
        registerDoc("string", "compareTo", "So sánh thứ tự từ điển giữa hai chuỗi (có phân biệt hoa thường).");
        registerDoc("string", "compareToIgnoreCase", "So sánh thứ tự từ điển không phân biệt hoa thường.");
        registerDoc("string", "trim", "Loại bỏ khoảng trắng ASCII ở đầu và cuối chuỗi.");
        registerDoc("string", "lines", "Tách chuỗi thành Stream các dòng (Java 11+).");
        registerDoc("string", "toCharArray", "Chuyển chuỗi thành mảng char mới độc lập.");
        registerDoc("string", "intern", "Đưa chuỗi vào string pool và trả về tham chiếu canonical.");
        registerDoc("string", "getBytes", "Mã hóa chuỗi thành mảng byte theo charset mặc định (hoặc chỉ định).");
        registerDoc("string", "codePointAt", "Trả về mã Unicode (code point) tại vị trí index.");
        registerDoc("string", "codePointBefore", "Trả về code point ngay trước vị trí index.");
        registerDoc("string", "codePointCount", "Đếm số code point trong khoảng [begin, end).");
        // CharSequence (String, StringBuilder, StringBuffer, v.v.)
        registerDoc("charsequence", "length", "Trả về số lượng ký tự hiện có trong chuỗi/chuỗi thay đổi được.");
        registerDoc("charsequence", "charAt", "Lấy ký tự tại vị trí index (0-based).");
        registerDoc("charsequence", "subSequence", "Trả về CharSequence con trong khoảng [start, end).");
        registerDoc("charsequence", "isEmpty", "Trả về true khi length() == 0.");
        // StringBuilder / StringBuffer
        registerDoc("stringbuilder", "append", "Ghép thêm giá trị vào cuối bộ đệm mà không tạo chuỗi tạm.");
        registerDoc("stringbuilder", "insert", "Chèn giá trị tại vị trí chỉ định trong bộ đệm.");
        registerDoc("stringbuilder", "delete", "Xóa đoạn ký tự trong bộ đệm theo chỉ số bắt đầu/kết thúc.");
        registerDoc("stringbuilder", "deleteCharAt", "Xóa ký tự tại chỉ số cho trước trong bộ đệm.");
        registerDoc("stringbuilder", "replace", "Thay thế đoạn ký tự bằng chuỗi mới.");
        registerDoc("stringbuilder", "reverse", "Đảo ngược thứ tự ký tự trong bộ đệm.");
        registerDoc("stringbuilder", "charAt", "Trả về ký tự tại chỉ số (0-based) trong bộ đệm.");
        registerDoc("stringbuilder", "substring", "Tạo chuỗi mới với đoạn ký tự được chỉ định.");
        registerDoc("stringbuilder", "subSequence", "Trả về CharSequence con trong khoảng [start, end).");
        registerDoc("stringbuilder", "indexOf", "Tìm vị trí xuất hiện đầu tiên của chuỗi con.");
        registerDoc("stringbuilder", "lastIndexOf", "Tìm vị trí xuất hiện cuối cùng của chuỗi con.");
        registerDoc("stringbuilder", "getChars", "Sao chép một đoạn ký tự sang mảng char được cấp phát.");
        registerDoc("stringbuilder", "length", "Trả về số lượng ký tự hiện có trong bộ đệm.");
        registerDoc("stringbuilder", "capacity", "Dung lượng đệm hiện tại trước khi cần cấp phát thêm.");
        registerDoc("stringbuilder", "ensureCapacity", "Đảm bảo bộ đệm có tối thiểu dung lượng nhất định để tránh cấp phát lại.");
        registerDoc("stringbuilder", "setLength", "Cắt ngắn hoặc mở rộng bộ đệm tới độ dài mong muốn (phần thêm chứa ký tự \\u0000).");
        registerDoc("stringbuilder", "trimToSize", "Giảm capacity xuống bằng length hiện tại để tiết kiệm RAM.");
        registerDoc("stringbuilder", "toString", "Chuyển nội dung bộ đệm thành đối tượng String bất biến.");
        // StringJoiner
        registerDoc("stringjoiner", "add", "Thêm thành phần mới với delimiter, prefix, suffix định nghĩa sẵn.");
        registerDoc("stringjoiner", "merge", "Ghép nối nội dung của StringJoiner khác vào hiện tại.");
        registerDoc("stringjoiner", "length", "Trả về độ dài kết quả hiện có sau khi ghép chuỗi.");
        registerDoc("stringjoiner", "setEmptyValue", "Thiết lập chuỗi trả về khi chưa thêm phần tử nào.");
        registerDoc("stringjoiner", "toString", "Xuất chuỗi hoàn chỉnh kèm prefix/suffix.");
// Collections utility class
        registerDoc("collections", "sort", "Sắp xếp danh sách theo thứ tự tự nhiên hoặc Comparator cung cấp.");
        registerDoc("collections", "reverse", "Đảo ngược thứ tự phần tử trong danh sách.");
        registerDoc("collections", "shuffle", "Xáo trộn ngẫu nhiên các phần tử trong danh sách.");
        registerDoc("collections", "max", "Tìm phần tử lớn nhất trong collection dựa trên Comparator tự nhiên hoặc truyền vào.");
        registerDoc("collections", "min", "Tìm phần tử nhỏ nhất trong collection.");
        registerDoc("collections", "copy", "Chép nội dung từ danh sách nguồn sang đích (đích phải có kích thước đủ).");
        registerDoc("collections", "swap", "Hoán đổi vị trí hai phần tử trong danh sách.");
        registerDoc("collections", "disjoint", "Trả về true nếu hai collection không có phần tử nào trùng nhau.");
        registerDoc("collections", "binarySearch", "Tìm vị trí phần tử trong danh sách đã sắp xếp bằng tìm kiếm nhị phân.");
        registerDoc("collections", "frequency", "Đếm số lần xuất hiện của phần tử trong collection.");
        registerDoc("collections", "indexOfSubList", "Tìm vị trí xuất hiện đầu tiên của dãy con trong danh sách nguồn.");
        registerDoc("collections", "lastIndexOfSubList", "Tìm vị trí xuất hiện cuối cùng của dãy con trong danh sách nguồn.");
        registerDoc("collections", "unmodifiableList", "Trả về danh sách chỉ đọc (ném UnsupportedOperationException khi chỉnh sửa).");
        registerDoc("collections", "unmodifiableSet", "Trả về tập hợp chỉ đọc.");
        registerDoc("collections", "unmodifiableMap", "Trả về bảng ánh xạ chỉ đọc.");
        registerDoc("collections", "emptyList", "Trả về danh sách rỗng bất biến.");
        registerDoc("collections", "emptySet", "Trả về tập hợp rỗng bất biến.");
        registerDoc("collections", "emptyMap", "Trả về bảng ánh xạ rỗng bất biến.");
        registerDoc("collections", "singletonList", "Tạo danh sách bất biến chỉ chứa một phần tử.");
        registerDoc("collections", "singleton", "Tạo tập hợp bất biến chỉ chứa một phần tử.");
        registerDoc("collections", "synchronizedList", "Bọc danh sách để an toàn trong môi trường đa luồng bằng đồng bộ hóa.");
        registerDoc("collections", "synchronizedMap", "Bọc bảng ánh xạ để an toàn đa luồng.");

// Arrays utility class
        registerDoc("arrays", "sort", "Sắp xếp mảng theo thứ tự tự nhiên hoặc Comparator (với Object[]).");
        registerDoc("arrays", "parallelSort", "Sắp xếp mảng lớn bằng thuật toán song song (Java 8+).");
        registerDoc("arrays", "binarySearch", "Tìm kiếm nhị phân phần tử trong mảng đã sắp xếp.");
        registerDoc("arrays", "fill", "Gán toàn bộ phần tử mảng thành giá trị cho trước.");
        registerDoc("arrays", "copyOf", "Tạo mảng mới có độ dài mong muốn từ mảng gốc.");
        registerDoc("arrays", "copyOfRange", "Sao chép một đoạn con của mảng sang mảng mới.");
        registerDoc("arrays", "equals", "So sánh hai mảng một chiều theo từng phần tử.");
        registerDoc("arrays", "deepEquals", "So sánh hai mảng (kể cả mảng lồng nhau) theo đệ quy.");
        registerDoc("arrays", "toString", "Chuyển mảng thành chuỗi mô tả dạng [a, b, c].");
        registerDoc("arrays", "deepToString", "Chuyển mảng đa chiều thành chuỗi đệ quy.");
        registerDoc("arrays", "hashCode", "Tạo mã băm cho mảng (một chiều).");
        registerDoc("arrays", "deepHashCode", "Tạo mã băm đệ quy cho mảng đa chiều.");
        registerDoc("arrays", "stream", "Chuyển mảng thành Stream để xử lý theo phong cách hàm.");
        registerDoc("arrays", "asList", "Bọc mảng thành danh sách kích thước cố định (thay đổi mảng sẽ phản ánh lên danh sách).");
        registerDoc("arrays", "setAll", "Gán lại từng phần tử dựa trên IntFunction cung cấp (thường dùng để khởi tạo theo công thức).");
        registerDoc("arrays", "compare", "So sánh hai mảng theo thứ tự từ điển, trả về âm/0/dương giống compareTo (Java 9+).");

// Nền tảng OOP (Object/Objects/Thread/Comparator/Iterator)
        registerDoc("object", "toString", "Trả về chuỗi mô tả đối tượng; nên override để debug và logging.");
        registerDoc("object", "equals", "So sánh bằng nhau theo nghiệp vụ; luôn đi kèm hashCode.");
        registerDoc("object", "hashCode", "Giá trị băm phục vụ Map/Set; phải phản ánh các thuộc tính tham gia equals.");
        registerDoc("object", "finalize", "Hook dọn dẹp trước khi GC thu hồi (đã lỗi thời, nên dùng try-with-resources).");
        registerDoc("object", "clone", "Tạo bản sao nông của đối tượng (cần implements Cloneable).");
        registerDoc("object", "getClass", "Lấy thông tin lớp runtime của đối tượng.");
        registerDoc("object", "wait", "Tạm dừng thread hiện tại tới khi có notify/timeout trên cùng monitor.");
        registerDoc("object", "notify", "Đánh thức một thread đang wait trên cùng monitor.");
        registerDoc("object", "notifyAll", "Đánh thức toàn bộ thread đang wait trên cùng monitor.");

        registerDoc("objects", "requireNonNull", "Kiểm tra tham số khác null, ném NullPointerException nếu sai.");
        registerDoc("objects", "isNull", "Predicate kiểm tra giá trị là null.");
        registerDoc("objects", "nonNull", "Predicate kiểm tra giá trị khác null.");
        registerDoc("objects", "equals", "So sánh hai giá trị, an toàn với tham số null.");
        registerDoc("objects", "deepEquals", "So sánh sâu mảng hoặc đối tượng lồng nhau.");
        registerDoc("objects", "hash", "Tính hashCode tổng hợp từ nhiều thuộc tính.");
        registerDoc("objects", "toString", "Tạo chuỗi mô tả, hỗ trợ giá trị null.");
        registerDoc("objects", "compare", "So sánh hai giá trị bằng Comparator cung cấp.");

        registerDoc("thread", "start", "Tạo thread mới và gọi run() đúng một lần.");
        registerDoc("thread", "run", "Nội dung công việc của thread khi start.");
        registerDoc("thread", "join", "Chờ thread hoàn thành hoặc hết timeout.");
        registerDoc("thread", "sleep", "Ngủ trong khoảng thời gian (ms), có thể bị interrupt.");
        registerDoc("thread", "yield", "Nhường CPU cho thread khác cùng ưu tiên (gợi ý, không đảm bảo).");
        registerDoc("thread", "interrupt", "Đánh dấu thread bị ngắt để dừng an toàn.");
        registerDoc("thread", "isInterrupted", "Kiểm tra cờ interrupt hiện tại.");
        registerDoc("thread", "isAlive", "Cho biết thread đã start và chưa kết thúc.");
        registerDoc("thread", "setDaemon", "Đánh dấu thread nền (không giữ JVM sống).");
        registerDoc("thread", "setPriority", "Thiết lập mức ưu tiên (1-10) cho thread.");
        registerDoc("thread", "getName", "Trả về tên hiện tại của thread.");
        registerDoc("thread", "setName", "Đặt tên thread cho dễ debug.");
        registerDoc("thread", "currentThread", "Trả về đối tượng Thread đại diện cho luồng đang chạy (static).");

        registerDoc("runnable", "run", "Phương thức mô tả công việc chạy không trả kết quả.");
        registerDoc("callable", "call", "Công việc trả về giá trị và có thể ném checked exception.");

        registerDoc("comparable", "compareTo", "Định nghĩa thứ tự tự nhiên giữa hai đối tượng cùng kiểu.");
        registerDoc("comparator", "compare", "So sánh hai đối tượng; trả âm/0/dương cho </=/>>.");
        registerDoc("comparator", "reversed", "Tạo Comparator đảo chiều.");
        registerDoc("comparator", "thenComparing", "Ghép nhiều tiêu chí so sánh tuần tự.");
        registerDoc("comparator", "comparing", "Tạo Comparator từ Function trích xuất khóa.");
        registerDoc("comparator", "naturalOrder", "Comparator mặc định theo Comparable.");
        registerDoc("comparator", "reverseOrder", "Comparator đảo của thứ tự tự nhiên.");

        // Functional interfaces
        registerDoc("predicate", "test", "Nhận T, trả về true/false dựa trên điều kiện.");
        registerDoc("function", "apply", "Chuyển đối số T thành kết quả R.");
        registerDoc("consumer", "accept", "Thực thi hành động với giá trị T, không trả kết quả.");
        registerDoc("supplier", "get", "Cung cấp giá trị mới (lazy) mà không nhận tham số.");
        registerDoc("binaryoperator", "apply", "Nhận hai giá trị cùng kiểu và gộp thành một giá trị.");
        registerDoc("unaryoperator", "apply", "Nhận và trả về cùng một kiểu, thường dùng để biến đổi.");

        registerDoc("iterable", "iterator", "Cung cấp Iterator để duyệt lần lượt.");
        registerDoc("iterable", "forEach", "Áp dụng Consumer cho từng phần tử (Java 8).");
        registerDoc("iterator", "hasNext", "Kiểm tra còn phần tử kế tiếp không.");
        registerDoc("iterator", "next", "Trả về phần tử hiện tại và tiến iterator.");
        registerDoc("iterator", "remove", "Xóa phần tử vừa trả về (tùy implement).");
        registerDoc("iterator", "forEachRemaining", "Duyệt toàn bộ phần tử còn lại bằng Consumer.");

// Core collection interfaces (List/Set/Queue/Map)
        registerDoc("list", "add", "Thêm phần tử vào cuối danh sách, tăng kích thước lên 1.");
        registerDoc("list", "addAll", "Ghép toàn bộ phần tử từ collection khác vào cuối danh sách.");
        registerDoc("list", "get", "Trả về phần tử tại vị trí (index) cho trước.");
        registerDoc("list", "set", "Ghi đè phần tử tại vị trí cho trước và trả về giá trị cũ.");
        registerDoc("list", "remove", "Xóa phần tử theo index hoặc giá trị (tùy overload) khỏi danh sách.");
        registerDoc("list", "removeIf", "Xóa các phần tử thỏa điều kiện Predicate.");
        registerDoc("list", "contains", "Kiểm tra xem danh sách có chứa phần tử cụ thể hay không.");
        registerDoc("list", "clear", "Xóa toàn bộ phần tử khỏi danh sách.");
        registerDoc("list", "size", "Trả về số lượng phần tử hiện có trong danh sách.");
        registerDoc("list", "isEmpty", "Kiểm tra danh sách có đang rỗng hay không.");
        registerDoc("list", "indexOf", "Trả về chỉ số đầu tiên của phần tử (hoặc -1 nếu không tồn tại).");
        registerDoc("list", "lastIndexOf", "Trả về chỉ số cuối cùng của phần tử (hoặc -1 nếu không tồn tại).");
        registerDoc("list", "sort", "Sắp xếp danh sách theo Comparator (Java 8+).");
        registerDoc("list", "stream", "Tạo Stream duyệt qua phần tử danh sách.");
        registerDoc("list", "of", "Tạo danh sách bất biến từ các phần tử truyền vào (Java 9+).");
        registerDoc("list", "subList", "Trả về view con của danh sách trong khoảng chỉ số cho trước.");
        registerDoc("list", "replaceAll", "Áp dụng UnaryOperator để thay thế từng phần tử ngay tại chỗ.");
        registerDoc("list", "toArray", "Chuyển nội dung danh sách sang mảng (truyền mảng mẫu để giữ kiểu).");

        registerDoc("set", "add", "Thêm phần tử (nếu chưa tồn tại) vào tập hợp.");
        registerDoc("set", "remove", "Xóa phần tử khỏi tập hợp nếu tồn tại.");
        registerDoc("set", "contains", "Kiểm tra phần tử có nằm trong tập hợp hay không.");
        registerDoc("set", "size", "Trả về số phần tử hiện có trong tập hợp.");
        registerDoc("set", "isEmpty", "Kiểm tra tập hợp có rỗng hay không.");
        registerDoc("set", "clear", "Xóa sạch tập hợp.");
        registerDoc("set", "addAll", "Hợp nhất toàn bộ phần tử từ collection khác vào set.");
        registerDoc("set", "retainAll", "Chỉ giữ lại các phần tử cũng có trong collection truyền vào.");
        registerDoc("set", "removeAll", "Loại bỏ mọi phần tử xuất hiện trong collection truyền vào.");
        registerDoc("set", "iterator", "Cung cấp Iterator duyệt qua từng phần tử của Set.");

        registerDoc("collection", "stream", "Tạo Stream tuần tự duyệt qua collection.");
        registerDoc("collection", "parallelStream", "Tạo Stream song song để tận dụng đa lõi.");
        registerDoc("collection", "removeIf", "Xóa các phần tử thỏa Predicate trực tiếp trên collection.");
        registerDoc("collection", "spliterator", "Cung cấp Spliterator để duyệt/phân tách phần tử.");

        registerDoc("queue", "offer", "Thêm phần tử vào cuối hàng đợi; trả về false nếu không thể thêm.");
        registerDoc("queue", "add", "Thêm phần tử vào hàng đợi, ném ngoại lệ nếu hết chỗ.");
        registerDoc("queue", "poll", "Lấy và loại bỏ phần tử đầu hàng; trả về null nếu hàng rỗng.");
        registerDoc("queue", "peek", "Xem phần tử đầu hàng mà không loại bỏ.");
        registerDoc("queue", "push", "Đẩy phần tử lên đầu cấu trúc (Deque/Stack).");
        registerDoc("queue", "pop", "Lấy và loại bỏ phần tử đầu cấu trúc (Deque/Stack).");

        registerDoc("map", "put", "Thêm hoặc cập nhật cặp khóa-giá trị; trả về giá trị cũ nếu tồn tại.");
        registerDoc("map", "putIfAbsent", "Chỉ thêm khóa-giá trị khi khóa chưa tồn tại.");
        registerDoc("map", "get", "Trả về giá trị tương ứng với khóa (hoặc null nếu không tồn tại).");
        registerDoc("map", "getOrDefault", "Lấy giá trị theo khóa và trả về mặc định nếu null.");
        registerDoc("map", "containsKey", "Kiểm tra sự tồn tại của khóa.");
        registerDoc("map", "containsValue", "Kiểm tra sự tồn tại của giá trị.");
        registerDoc("map", "remove", "Xóa khóa (cả overload key hoặc key/value trả về boolean) khỏi bảng ánh xạ.");
        registerDoc("map", "replace", "Thay thế giá trị mới cho khóa khi khóa đang tồn tại.");
        registerDoc("map", "merge", "Hợp nhất giá trị mới với giá trị cũ bằng hàm mergeFunction.");
        registerDoc("map", "forEach", "Duyệt từng Entry<K,V> bằng BiConsumer.");
        registerDoc("map", "replaceAll", "Áp dụng BiFunction để cập nhật lại toàn bộ giá trị.");
        registerDoc("map", "compute", "Tính toán lại giá trị cho key (có thể trả null để xóa).");
        registerDoc("map", "computeIfAbsent", "Tạo giá trị khi khóa chưa tồn tại bằng Function cung cấp.");
        registerDoc("map", "computeIfPresent", "Cập nhật giá trị khi khóa đang tồn tại bằng BiFunction.");
        registerDoc("map", "size", "Trả về số cặp khóa-giá trị hiện có.");
        registerDoc("map", "isEmpty", "Kiểm tra bảng ánh xạ có rỗng hay không.");
        registerDoc("map", "clear", "Xóa toàn bộ cặp khóa-giá trị.");
        registerDoc("map", "keySet", "Trả về tập hợp view của tất cả khóa.");
        registerDoc("map", "values", "Trả về collection view của tất cả giá trị.");
        registerDoc("map", "entrySet", "Trả về tập hợp view các Entry (key, value).");
        registerDoc("map", "of", "Tạo bảng ánh xạ bất biến từ các cặp khóa-giá trị liệt kê (Java 9+).");
        registerDoc("map", "ofEntries", "Tạo bảng ánh xạ bất biến từ các Entry có sẵn (Java 9+).");

        registerDoc("map.entry", "getKey", "Lấy khóa (key) của cặp entry.");
        registerDoc("map.entry", "getValue", "Lấy giá trị (value) hiện tại của entry.");
        registerDoc("map.entry", "setValue", "Cập nhật giá trị bên trong entry và trả về giá trị cũ.");

// IO, đường dẫn & công cụ nhập liệu
        registerDoc("file", "exists", "Kiểm tra tập tin/thư mục có tồn tại trên đĩa.");
        registerDoc("file", "createNewFile", "Tạo tập tin mới rỗng; trả false nếu đã tồn tại.");
        registerDoc("file", "mkdir", "Tạo một thư mục cấp đơn, thất bại nếu thiếu thư mục cha.");
        registerDoc("file", "mkdirs", "Tạo thư mục và toàn bộ thư mục cha nếu chưa có.");
        registerDoc("file", "isDirectory", "Trả về true nếu đường dẫn trỏ tới thư mục.");
        registerDoc("file", "isFile", "Trả về true nếu đường dẫn trỏ tới tập tin thường.");
        registerDoc("file", "canRead", "Kiểm tra quyền đọc tập tin/thu mục.");
        registerDoc("file", "canWrite", "Kiểm tra quyền ghi.");
        registerDoc("file", "setReadable", "Thay đổi quyền đọc (đối với user hiện hành hoặc toàn hệ thống).");
        registerDoc("file", "setWritable", "Thay đổi quyền ghi.");
        registerDoc("file", "renameTo", "Đổi tên hoặc di chuyển tập tin tới đường dẫn đích.");
        registerDoc("file", "listFiles", "Liệt kê các tập tin/con trong thư mục.");
        registerDoc("file", "getName", "Tên tập tin/thu mục (không gồm đường dẫn).");
        registerDoc("file", "getAbsolutePath", "Đường dẫn tuyệt đối của File.");
        registerDoc("file", "length", "Kích thước tập tin (bytes).");
        registerDoc("file", "lastModified", "Timestamp lần chỉnh sửa cuối cùng (milliseconds).");
        registerDoc("file", "delete", "Xóa tập tin hoặc thư mục trống.");

        registerDoc("path", "resolve", "Ghép thêm đoạn đường dẫn con vào Path hiện tại.");
        registerDoc("path", "relativize", "Tính đường dẫn tương đối giữa hai Path.");
        registerDoc("path", "normalize", "Loại bỏ . và .. dư thừa trong đường dẫn.");
        registerDoc("path", "startsWith", "Kiểm tra Path bắt đầu bằng tiền tố cho trước.");
        registerDoc("path", "endsWith", "Kiểm tra Path kết thúc với hậu tố cho trước.");
        registerDoc("path", "toAbsolutePath", "Chuyển Path về dạng tuyệt đối.");
        registerDoc("path", "toString", "Chuỗi biểu diễn đường dẫn tùy nền tảng.");
        registerDoc("path", "getFileName", "Trả về tên phần tử cuối cùng (có thể null nếu Path rỗng).");
        registerDoc("path", "getParent", "Path cha trực tiếp; null nếu ở gốc.");
        registerDoc("path", "toFile", "Chuyển Path thành java.io.File.");

        registerDoc("paths", "get", "Phương thức tiện dụng tạo Path từ chuỗi hoặc URI (static).");

        registerDoc("files", "exists", "Kiểm tra đường dẫn có tồn tại.");
        registerDoc("files", "createFile", "Tạo mới một file rỗng tại Path chỉ định (ném nếu đã có).");
        registerDoc("files", "readAllLines", "Đọc toàn bộ file văn bản thành List<String> (UTF-8 mặc định).");
        registerDoc("files", "readAllBytes", "Đọc toàn bộ file vào mảng byte.");
        registerDoc("files", "write", "Ghi dữ liệu (byte/chuỗi) xuống tập tin.");
        registerDoc("files", "copy", "Sao chép file hoặc thư mục.");
        registerDoc("files", "move", "Di chuyển hoặc đổi tên file.");
        registerDoc("files", "delete", "Xóa file (ném IOException nếu thất bại).");
        registerDoc("files", "size", "Trả về kích thước file (bytes).");
        registerDoc("files", "getLastModifiedTime", "Đọc thời điểm chỉnh sửa cuối cùng dưới dạng FileTime.");
        registerDoc("files", "isDirectory", "Kiểm tra Path có phải thư mục (theo thuộc tính filesystem).");
        registerDoc("files", "isReadable", "Kiểm tra có quyền đọc đường dẫn.");
        registerDoc("files", "isWritable", "Kiểm tra có quyền ghi.");
        registerDoc("files", "createDirectories", "Tạo toàn bộ cây thư mục nếu chưa tồn tại.");
        registerDoc("files", "lines", "Trả về Stream<String> đọc từng dòng theo kiểu lười (lazy).");
        registerDoc("files", "walk", "Duyệt cây thư mục theo chiều sâu (trả Stream<Path>).");
        registerDoc("files", "find", "Tìm các Path thỏa Predicate trong phạm vi depth cho trước.");
        registerDoc("files", "list", "Trả về Stream<Path> liệt kê trực tiếp nội dung thư mục.");
        registerDoc("files", "isSymbolicLink", "Kiểm tra đường dẫn là symlink hay không.");
        registerDoc("files", "newBufferedReader", "Mở BufferedReader theo charset mặc định để đọc văn bản.");
        registerDoc("files", "newBufferedWriter", "Mở BufferedWriter để ghi văn bản với charset mặc định.");

        registerDoc("inputstream", "read", "Đọc byte tiếp theo hoặc trả -1 khi hết dữ liệu.");
        registerDoc("inputstream", "skip", "Bỏ qua số byte chỉ định (có thể ít hơn yêu cầu).");
        registerDoc("inputstream", "available", "Ước lượng số byte có thể đọc mà không block.");
        registerDoc("inputstream", "mark", "Đánh dấu vị trí để có thể reset trở lại.");
        registerDoc("inputstream", "reset", "Quay lại vị trí đã mark (nếu stream hỗ trợ).");
        registerDoc("inputstream", "close", "Giải phóng tài nguyên IO.");

        registerDoc("outputstream", "write", "Ghi byte hoặc mảng byte ra đích.");
        registerDoc("outputstream", "flush", "Đẩy dữ liệu đang đệm xuống thiết bị thật.");
        registerDoc("outputstream", "close", "Đóng stream và flush nếu cần.");

        registerDoc("reader", "read", "Đọc ký tự vào buffer, trả số ký tự đọc được hoặc -1.");
        registerDoc("reader", "ready", "Cho biết Reader có sẵn dữ liệu để đọc hay không.");
        registerDoc("reader", "close", "Đóng nguồn đọc.");
        registerDoc("writer", "write", "Ghi ký tự/chuỗi vào đích.");
        registerDoc("writer", "append", "Ghi thêm ký tự vào cuối nguồn ghi.");
        registerDoc("writer", "flush", "Đảm bảo dữ liệu đã ra thiết bị.");
        registerDoc("writer", "close", "Đóng nguồn ghi.");

        registerDoc("scanner", "next", "Đọc token tiếp theo theo delimiter hiện tại.");
        registerDoc("scanner", "nextLine", "Đọc toàn bộ phần còn lại của dòng hiện tại.");
        registerDoc("scanner", "nextInt", "Parse token tiếp theo thành số nguyên.");
        registerDoc("scanner", "nextDouble", "Parse token thành số thực double.");
        registerDoc("scanner", "nextBoolean", "Đọc giá trị true/false (không phân biệt hoa thường).");
        registerDoc("scanner", "hasNext", "Kiểm tra còn token kế tiếp hay không.");
        registerDoc("scanner", "hasNextInt", "Kiểm tra token tiếp theo có parse được sang int hay không.");
        registerDoc("scanner", "hasNextDouble", "Kiểm tra token tiếp theo có parse được sang double hay không.");
        registerDoc("scanner", "hasNextLine", "Kiểm tra còn dòng tiếp theo hay không.");
        registerDoc("scanner", "useDelimiter", "Đổi biểu thức phân tách token.");
        registerDoc("scanner", "delimiter", "Lấy Pattern delimiter hiện tại.");
        registerDoc("scanner", "close", "Giải phóng nguồn dữ liệu gắn với Scanner.");

// Stream API
        registerDoc("stream", "map", "Biến đổi mỗi phần tử Stream bằng Function thành giá trị mới.");
        registerDoc("stream", "filter", "Giữ lại những phần tử thỏa Predicate.");
        registerDoc("stream", "flatMap", "Biến đổi phần tử thành Stream con rồi gộp lại.");
        registerDoc("stream", "peek", "Chèn bước theo dõi phụ (không thay đổi dữ liệu).");
        registerDoc("stream", "sorted", "Sắp xếp Stream theo thứ tự tự nhiên hoặc Comparator.");
        registerDoc("stream", "distinct", "Loại bỏ các phần tử trùng theo equals.");
        registerDoc("stream", "limit", "Chỉ lấy N phần tử đầu tiên trong Stream.");
        registerDoc("stream", "skip", "Bỏ qua N phần tử đầu tiên.");
        registerDoc("stream", "forEach", "Duyệt từng phần tử và thực thi Consumer tương ứng.");
        registerDoc("stream", "collect", "Thu thập Stream thành kết quả cuối cùng bằng Collector.");
        registerDoc("stream", "reduce", "Gộp các phần tử thành một giá trị duy nhất thông qua accumulator.");
        registerDoc("stream", "count", "Đếm số phần tử trong Stream.");
        registerDoc("stream", "findFirst", "Trả về phần tử đầu tiên (Optional) trong Stream tuần tự.");
        registerDoc("stream", "findAny", "Trả về một phần tử bất kỳ (Optional), hữu ích cho Stream song song.");
        registerDoc("stream", "anyMatch", "Kiểm tra có phần tử nào thỏa Predicate hay không.");
        registerDoc("stream", "allMatch", "Kiểm tra toàn bộ phần tử có thỏa Predicate hay không.");
        registerDoc("stream", "noneMatch", "Kiểm tra không có phần tử nào thỏa Predicate.");
        registerDoc("stream", "toArray", "Thu thập Stream thành mảng (có thể truyền IntFunction để tùy kiểu).");

        registerDoc("collectors", "toList", "Collector thu thập phần tử Stream thành List mới.");
        registerDoc("collectors", "toSet", "Collector thu thập phần tử Stream thành Set.");
        registerDoc("collectors", "toMap", "Collector thu thập thành Map với hàm xác định khóa và giá trị.");
        registerDoc("collectors", "joining", "Collector nối chuỗi bằng phân tách (và tùy chọn prefix/suffix).");
        registerDoc("collectors", "counting", "Collector đếm số phần tử.");
        registerDoc("collectors", "summarizingInt", "Collector thống kê int (count, sum, min, max, avg).");

// Optional API
        registerDoc("optional", "of", "Tạo Optional chứa giá trị không null (ném NullPointerException nếu null).");
        registerDoc("optional", "ofNullable", "Tạo Optional chứa giá trị nếu không null, rỗng nếu null.");
        registerDoc("optional", "empty", "Tạo Optional rỗng.");
        registerDoc("optional", "get", "Lấy giá trị bên trong; ném NoSuchElementException nếu rỗng.");
        registerDoc("optional", "isPresent", "Kiểm tra Optional có đang chứa giá trị không.");
        registerDoc("optional", "ifPresent", "Thực thi Consumer nếu Optional chứa giá trị.");
        registerDoc("optional", "map", "Biến đổi giá trị bên trong Optional bằng Function và bọc lại.");
        registerDoc("optional", "flatMap", "Biến đổi giá trị thành Optional khác và trả về trực tiếp.");
        registerDoc("optional", "filter", "Giữ giá trị nếu Predicate trả true, ngược lại trở thành Optional rỗng.");
        registerDoc("optional", "orElse", "Trả về giá trị nếu có, ngược lại dùng giá trị mặc định.");
        registerDoc("optional", "orElseGet", "Dùng Supplier sinh giá trị mặc định khi Optional rỗng.");
        registerDoc("optional", "orElseThrow", "Ném ngoại lệ do Supplier cung cấp nếu Optional rỗng.");

// Math / System / PrintStream
        registerDoc("math", "abs", "Lấy giá trị tuyệt đối của số truyền vào.");
        registerDoc("math", "max", "Trả về đối số lớn hơn trong hai số.");
        registerDoc("math", "min", "Trả về đối số nhỏ hơn trong hai số.");
        registerDoc("math", "pow", "Tính lũy thừa: cơ số^số mũ.");
        registerDoc("math", "sqrt", "Tính căn bậc hai.");
        registerDoc("math", "round", "Làm tròn số tới giá trị gần nhất.");
        registerDoc("math", "floor", "Làm tròn xuống.");
        registerDoc("math", "ceil", "Làm tròn lên.");
        registerDoc("math", "cbrt", "Tính căn bậc ba của một số thực.");
        registerDoc("math", "sin", "Giá trị sin, tham số radian.");
        registerDoc("math", "cos", "Giá trị cos, tham số radian.");
        registerDoc("math", "tan", "Giá trị tan, tham số radian.");
        registerDoc("math", "asin", "Hàm sin ngược, trả về góc (radian).");
        registerDoc("math", "acos", "Hàm cos ngược, trả về góc (radian).");
        registerDoc("math", "atan", "Hàm tan ngược với đầu vào là tỷ số y/x.");
        registerDoc("math", "atan2", "Hàm tan ngược hai biến, xác định góc đúng theo điểm (y, x).");
        registerDoc("math", "log", "Logarit tự nhiên (cơ số e).");
        registerDoc("math", "log10", "Logarit cơ số 10.");
        registerDoc("math", "exp", "Tính e^x.");
        registerDoc("math", "signum", "Trả -1, 0 hoặc 1 dựa trên dấu của tham số.");
        registerDoc("math", "toRadians", "Đổi độ sang radian.");
        registerDoc("math", "toDegrees", "Đổi radian sang độ.");
        registerDoc("math", "random", "Sinh số thực ngẫu nhiên trong khoảng [0,1).");

        registerDoc("system", "arraycopy", "Sao chép nhanh các phần tử giữa hai mảng (native, rất hiệu quả).");
        registerDoc("system", "currentTimeMillis", "Thời gian hiện tại tính bằng mili giây kể từ 1/1/1970 UTC.");
        registerDoc("system", "nanoTime", "Đồng hồ thời gian chính xác cao (nano giây) dùng đo hiệu năng.");
        registerDoc("system", "exit", "Kết thúc JVM với mã thoát được cung cấp.");
        registerDoc("system", "gc", "Gợi ý JVM thực hiện thu gom rác.");
        registerDoc("system", "getProperty", "Đọc giá trị của thuộc tính hệ thống theo khóa.");
        registerDoc("system", "setProperty", "Thiết lập/ghi đè thuộc tính hệ thống và trả về giá trị cũ.");
        registerDoc("system", "setOut", "Chuyển hướng System.out sang PrintStream khác (ví dụ ghi log).");
        registerDoc("system", "setErr", "Chuyển hướng System.err sang PrintStream khác.");

        registerDoc("printstream", "println", "In giá trị ra luồng và xuống dòng.");
        registerDoc("printstream", "print", "In giá trị ra luồng không xuống dòng.");
        registerDoc("printstream", "printf", "Định dạng chuỗi rồi in ra luồng theo mẫu (printf).");
        registerDoc("printstream", "format", "Tương đương printf nhưng trả lại PrintStream để chaining.");
        registerDoc("printstream", "flush", "Đẩy mọi dữ liệu còn đệm xuống thiết bị xuất.");

        // Character utility methods
        registerDoc("character", "isLetter", "Kiểm tra ký tự có thuộc nhóm chữ cái Unicode hay không.");
        registerDoc("character", "isAlphabetic", "Kiểm tra ký tự thuộc nhóm chữ (bao gồm letter và các dạng mở rộng).");
        registerDoc("character", "isDigit", "Kiểm tra ký tự là số (bao gồm chữ số đặc biệt Unicode).");
        registerDoc("character", "isWhitespace", "Kiểm tra ký tự khoảng trắng (space, tab, xuống dòng, ...).");
        registerDoc("character", "isUpperCase", "Kiểm tra ký tự chữ hoa.");
        registerDoc("character", "isLowerCase", "Kiểm tra ký tự chữ thường.");
        registerDoc("character", "toUpperCase", "Chuyển ký tự sang chữ hoa theo quy tắc Unicode.");
        registerDoc("character", "toLowerCase", "Chuyển ký tự sang chữ thường.");
        registerDoc("character", "getNumericValue", "Trả về giá trị số tương ứng (ví dụ 'Ⅳ' -> 4).");

        // Primitive wrapper helpers (Integer / Double)
        registerDoc("integer", "parseInt", "Chuyển chuỗi thành int, hỗ trợ cơ số tùy chọn.");
        registerDoc("integer", "valueOf", "Chuyển chuỗi hoặc int sang đối tượng Integer (có cache).");
        registerDoc("integer", "toString", "Biểu diễn số nguyên thành chuỗi.");
        registerDoc("integer", "compare", "So sánh hai int, trả về âm/0/dương.");
        registerDoc("integer", "max", "Trả về số lớn hơn giữa hai int.");
        registerDoc("integer", "min", "Trả về số nhỏ hơn giữa hai int.");
        registerDoc("integer", "sum", "Cộng hai giá trị int.");
        registerDoc("integer", "divideUnsigned", "Chia hai số nguyên không dấu (coi bit cao là phần độ lớn).");
        registerDoc("integer", "toUnsignedLong", "Chuyển int sang long không dấu (giữ nguyên bit pattern).");
        registerDoc("integer", "toHexString", "Biểu diễn int dạng chuỗi hexa.");
        registerDoc("integer", "bitCount", "Đếm số bit 1 trong biểu diễn nhị phân.");

        registerDoc("double", "parseDouble", "Chuyển chuỗi thành double (IEEE 754).");
        registerDoc("double", "isNaN", "Kiểm tra giá trị là NaN.");
        registerDoc("double", "isInfinite", "Kiểm tra giá trị vô cực.");
        registerDoc("double", "compare", "So sánh hai double theo tiêu chuẩn IEEE.");
        registerDoc("double", "toString", "Chuỗi biểu diễn double (có thể ở dạng khoa học).");
        registerDoc("double", "sum", "Cộng hai double.");
        registerDoc("double", "max", "Trả về double lớn hơn.");
        registerDoc("double", "min", "Trả về double nhỏ hơn.");
        registerDoc("double", "hashCode", "Tạo mã băm tương thích equals cho double.");
        registerDoc("double", "valueOf", "Boxing double nguyên thủy thành đối tượng Double.");

        // Random utilities
        registerDoc("random", "nextInt", "Sinh số int ngẫu nhiên; truyền bound để giới hạn [0, bound).");
        registerDoc("random", "nextLong", "Sinh số long ngẫu nhiên.");
        registerDoc("random", "nextDouble", "Sinh số double trong [0,1).");
        registerDoc("random", "nextFloat", "Sinh số float trong [0,1).");
        registerDoc("random", "nextBoolean", "Sinh giá trị true/false ngẫu nhiên.");
        registerDoc("random", "ints", "Tạo IntStream vô hạn/giới hạn các số ngẫu nhiên.");
        registerDoc("random", "doubles", "Tạo DoubleStream các số ngẫu nhiên.");
        registerDoc("random", "longs", "Tạo LongStream các số ngẫu nhiên.");

        // java.time date & time
        registerDoc("localdate", "now", "Lấy ngày hiện tại theo múi giờ mặc định.");
        registerDoc("localdate", "of", "Tạo LocalDate từ năm/tháng/ngày cụ thể.");
        registerDoc("localdate", "parse", "Phân tích chuỗi ISO (hoặc formatter tùy chọn) thành LocalDate.");
        registerDoc("localdate", "plusDays", "Cộng thêm n ngày (trả đối tượng mới).");
        registerDoc("localdate", "minusDays", "Trừ n ngày.");
        registerDoc("localdate", "plusMonths", "Cộng thêm n tháng, tự động chỉnh ngày.");
        registerDoc("localdate", "minusMonths", "Trừ n tháng.");
        registerDoc("localdate", "plusYears", "Cộng thêm n năm.");
        registerDoc("localdate", "minusYears", "Trừ n năm.");
        registerDoc("localdate", "getDayOfWeek", "Trả về enum DayOfWeek đại diện thứ trong tuần.");
        registerDoc("localdate", "getYear", "Trả về năm (kể cả âm).");
        registerDoc("localdate", "getMonthValue", "Trả về tháng 1-12.");
        registerDoc("localdate", "getDayOfMonth", "Ngày trong tháng (1-31).");
        registerDoc("localdate", "isBefore", "Kiểm tra ngày này đứng trước ngày khác.");
        registerDoc("localdate", "isAfter", "Kiểm tra ngày này đứng sau ngày khác.");
        registerDoc("localdate", "compareTo", "So sánh thứ tự thời gian giữa hai LocalDate.");

        registerDoc("localdatetime", "now", "Lấy thời điểm ngày/giờ hiện tại theo hệ thống.");
        registerDoc("localdatetime", "of", "Tạo LocalDateTime từ các thành phần năm-tháng-ngày-giờ-phút-giây.");
        registerDoc("localdatetime", "parse", "Phân tích chuỗi ISO thành LocalDateTime.");
        registerDoc("localdatetime", "plusDays", "Cộng thêm n ngày (bao gồm cả phần giờ phút).");
        registerDoc("localdatetime", "minusDays", "Trừ n ngày.");
        registerDoc("localdatetime", "minusHours", "Trừ đi n giờ.");
        registerDoc("localdatetime", "plusMonths", "Cộng thêm n tháng.");
        registerDoc("localdatetime", "minusMonths", "Trừ n tháng.");
        registerDoc("localdatetime", "plusYears", "Cộng thêm n năm.");
        registerDoc("localdatetime", "minusYears", "Trừ n năm.");
        registerDoc("localdatetime", "getDayOfWeek", "Lấy thứ trong tuần của LocalDateTime.");
        registerDoc("localdatetime", "getYear", "Trả về năm.");
        registerDoc("localdatetime", "getMonthValue", "Trả về tháng (1-12).");
        registerDoc("localdatetime", "getDayOfMonth", "Ngày trong tháng.");
        registerDoc("localdatetime", "isBefore", "Kiểm tra thời điểm trước thời điểm khác.");
        registerDoc("localdatetime", "isAfter", "Kiểm tra thời điểm sau.");
        registerDoc("localdatetime", "compareTo", "So sánh thứ tự hai LocalDateTime.");

        registerDoc("localtime", "now", "Lấy giờ hiện tại (không kèm ngày).");
        registerDoc("localtime", "of", "Tạo LocalTime từ giờ/phút (và tùy chọn giây, nano).");
        registerDoc("localtime", "parse", "Chuyển chuỗi theo định dạng ISO thành LocalTime.");
        registerDoc("localtime", "plusHours", "Cộng thêm n giờ.");
        registerDoc("localtime", "minusMinutes", "Trừ đi n phút.");
        registerDoc("localtime", "plusMinutes", "Cộng thêm n phút.");

        registerDoc("instant", "now", "Mốc thời gian UTC hiện tại, độ chính xác nano.");
        registerDoc("instant", "ofEpochMilli", "Tạo Instant từ mili-giây kể từ 1/1/1970.");
        registerDoc("instant", "toEpochMilli", "Chuyển Instant về mili-giây kể từ epoch.");

        registerDoc("duration", "between", "Khoảng thời gian giữa hai Instant/Temporal.");
        registerDoc("duration", "toHours", "Chuyển Duration về tổng số giờ (long).");
        registerDoc("duration", "toMinutes", "Chuyển Duration về tổng số phút.");
        registerDoc("duration", "getSeconds", "Trả về số giây của Duration (không gồm nanos).");

        // Exception helpers
        registerDoc("exception", "getMessage", "Thông điệp ngắn gọn mô tả lỗi.");
        registerDoc("exception", "getLocalizedMessage", "Thông điệp bản địa hóa (mặc định giống getMessage).");
        registerDoc("exception", "getCause", "Ngoại lệ gốc gây ra lỗi hiện tại.");
        registerDoc("exception", "initCause", "Gán nguyên nhân gốc cho ngoại lệ.");
        registerDoc("exception", "printStackTrace", "In stack trace ra luồng lỗi để debug.");
        registerDoc("exception", "fillInStackTrace", "Làm mới thông tin stack trace hiện tại (ít dùng).");

        // Arrays & primitives helper
        registerDoc("array", "length", "Thuộc tính trả về số phần tử hiện có trong mảng");
    }

    private static void aliasOwner(String owner, String canonical) {
        if (owner == null || canonical == null) {
            return;
        }
        OWNER_ALIASES.put(owner.toLowerCase(Locale.ROOT), canonical.toLowerCase(Locale.ROOT));
    }

    private static void registerDoc(String owner, String method, String doc) {
        if (owner == null || method == null || doc == null) {
            return;
        }
        BUILTIN_DOCS.computeIfAbsent(owner.toLowerCase(Locale.ROOT), key -> new LinkedHashMap<>())
                .put(method.toLowerCase(Locale.ROOT), doc);
    }

    private JTree fileTree;
    private DefaultTreeModel treeModel;
    private JTextField filterField;
    private JTabbedPane tabbedPane;
    private JTextField searchField;
    private JButton searchNextBtn;
    private JButton openDirBtn;
    private File currentRoot;
    private SwingWorker<DefaultMutableTreeNode, Void> treeLoader;
    private final Map<String, File> classFileCache = new HashMap<>();
    private final Map<String, List<MethodInfo>> inheritanceCache = new HashMap<>();

    public JavaSourceQuickView() {
        super(APP_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(scale(1000), scale(700));
        setLocationRelativeTo(null);
        getContentPane().setBackground(WINDOW_BACKGROUND);
        initUI();
    }

    private void initUI() {
        // Top toolbar
        JPanel topPanel = new JPanel(new BorderLayout(scale(12), scale(12)));
        topPanel.setOpaque(true);
        topPanel.setBackground(TOOLBAR_BACKGROUND);
        topPanel.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, BORDER_COLOR),
                new EmptyBorder(scale(12), scale(16), scale(12), scale(16))
        ));

        JPanel leftTop = new JPanel(new FlowLayout(FlowLayout.LEFT, scale(12), 0));
        leftTop.setOpaque(false);
        openDirBtn = new JButton("Open folder...");
        stylePrimaryButton(openDirBtn);
        openDirBtn.addActionListener(e -> pickFolder());
        leftTop.add(openDirBtn);

        filterField = new JTextField(20);
        filterField.setToolTipText("Filter files by name (use .java for only java files)");
        filterField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                refreshTree();
            }

            public void removeUpdate(DocumentEvent e) {
                refreshTree();
            }

            public void changedUpdate(DocumentEvent e) {
                refreshTree();
            }
        });
        filterField.setText(".java");
        styleTextField(filterField);
        leftTop.add(createToolbarLabel("Filter:"));
        leftTop.add(filterField);

        topPanel.add(leftTop, BorderLayout.WEST);

        JPanel rightTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, scale(12), 0));
        rightTop.setOpaque(false);
        searchField = new JTextField(24);
        styleTextField(searchField);
        searchField.setToolTipText("Search inside the currently opened file");
        searchNextBtn = new JButton("Find Next");
        stylePrimaryButton(searchNextBtn);
        searchNextBtn.addActionListener(e -> findNextInCurrentTab());
        searchField.addActionListener(e -> findNextInCurrentTab());
        rightTop.add(createToolbarLabel("Find in file:"));
        rightTop.add(searchField);
        rightTop.add(searchNextBtn);

        topPanel.add(rightTop, BorderLayout.EAST);

        // Split pane: tree | tabs
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("No folder opened");
        treeModel = new DefaultTreeModel(rootNode);
        fileTree = new JTree(treeModel);
        fileTree.setFont(UI_FONT);
        fileTree.setRootVisible(true);
        fileTree.setShowsRootHandles(true);
        fileTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        fileTree.setRowHeight(0);
        fileTree.setBackground(SURFACE_COLOR);
        DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) fileTree.getCellRenderer();
        renderer.setOpaque(true);
        renderer.setFont(UI_FONT);
        renderer.setTextNonSelectionColor(new Color(0x2f363d));
        renderer.setBackgroundNonSelectionColor(SURFACE_COLOR);
        renderer.setBackgroundSelectionColor(TREE_SELECTION_BG);
        renderer.setTextSelectionColor(TREE_SELECTION_TEXT);
        renderer.setBorderSelectionColor(TREE_SELECTION_BORDER);
        fileTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() != 2 || SwingUtilities.isRightMouseButton(e)) {
                    return;
                }
                TreePath path = fileTree.getPathForLocation(e.getX(), e.getY());
                if (path == null) {
                    return;
                }
                Object nodeObj = path.getLastPathComponent();
                if (!(nodeObj instanceof DefaultMutableTreeNode)) {
                    return;
                }
                Object userObject = ((DefaultMutableTreeNode) nodeObj).getUserObject();
                if (!(userObject instanceof FileTreeNode)) {
                    return;
                }
                File file = ((FileTreeNode) userObject).getFile();
                if (file.isFile()) {
                    openFileInTab(file);
                }
            }
        });

        JScrollPane treeScroll = new JScrollPane(fileTree);
        treeScroll.setMinimumSize(new Dimension(scale(220), scale(200)));
        treeScroll.getViewport().setBackground(SURFACE_COLOR);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UI_FONT);
        tabbedPane.setBackground(SURFACE_COLOR);
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScroll, tabbedPane);
        split.setDividerLocation(scale(300));
        split.setBorder(new MatteBorder(0, 0, 0, 0, BORDER_COLOR));

        add(topPanel, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);

        // Menu
        JMenuBar mb = new JMenuBar();
        mb.setBorder(new MatteBorder(0, 0, 1, 0, BORDER_COLOR));
        mb.setBackground(TOOLBAR_BACKGROUND);
        mb.setFont(UI_FONT);
        JMenu fileMenu = new JMenu("File");
        fileMenu.setFont(UI_FONT);
        JMenuItem openItem = new JMenuItem("Open folder...");
        openItem.setFont(UI_FONT);
        openItem.addActionListener(e -> pickFolder());
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setFont(UI_FONT);
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(openItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        mb.add(fileMenu);
        setJMenuBar(mb);
    }

    private void pickFolder() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int ret = chooser.showOpenDialog(this);
        if (ret == JFileChooser.APPROVE_OPTION) {
            currentRoot = chooser.getSelectedFile();
            classFileCache.clear();
            inheritanceCache.clear();
            loadTreeFromDirectory(currentRoot);
        }
    }

    private void refreshTree() {
        if (currentRoot != null) {
            loadTreeFromDirectory(currentRoot);
        }
    }

    private void loadTreeFromDirectory(File root) {
        if (treeLoader != null && !treeLoader.isDone()) {
            treeLoader.cancel(true);
        }

        DefaultMutableTreeNode loadingNode = new DefaultMutableTreeNode("Loading...");
        treeModel.setRoot(loadingNode);
        treeModel.reload();

        treeLoader = new SwingWorker<DefaultMutableTreeNode, Void>() {
            @Override
            protected DefaultMutableTreeNode doInBackground() {
                String filter = filterField.getText().trim().toLowerCase(Locale.ROOT);
                return buildTreeModel(root, filter, this, true);
            }

            @Override
            protected void done() {
                try {
                    if (isCancelled()) {
                        return;
                    }
                    DefaultMutableTreeNode newRoot = get();
                    treeModel.setRoot(newRoot);
                    treeModel.reload();
                    expandAllRows();
                } catch (CancellationException ignored) {
                    // another refresh has taken over
                } catch (ExecutionException ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    JOptionPane.showMessageDialog(JavaSourceQuickView.this,
                            "Failed to load folder: " + cause.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                } finally {
                    if (treeLoader == this) {
                        treeLoader = null;
                    }
                }
            }
        };
        treeLoader.execute();
    }

    private DefaultMutableTreeNode buildTreeModel(File directory, String filter, SwingWorker<?, ?> worker, boolean isRoot) {
        if (worker.isCancelled()) {
            throw new CancellationException();
        }

        DefaultMutableTreeNode node = new DefaultMutableTreeNode(new FileTreeNode(directory));
        File[] entries = directory.listFiles();
        if (entries == null) {
            return node;
        }

        Arrays.sort(entries, (a, b) -> {
            if (a.isDirectory() && !b.isDirectory()) {
                return -1;
            }
            if (!a.isDirectory() && b.isDirectory()) {
                return 1;
            }
            return a.getName().compareToIgnoreCase(b.getName());
        });

        boolean hasMatches = false;
        for (File entry : entries) {
            if (worker.isCancelled()) {
                throw new CancellationException();
            }
            if (!entry.canRead()) {
                continue;
            }
            if (entry.isDirectory()) {
                DefaultMutableTreeNode childNode = buildTreeModel(entry, filter, worker, false);
                if (childNode != null) {
                    node.add(childNode);
                    hasMatches = true;
                }
            } else {
                String lowerName = entry.getName().toLowerCase(Locale.ROOT);
                if (!lowerName.endsWith(".java")) {
                    continue;
                }
                if (!filter.isEmpty() && !lowerName.contains(filter)) {
                    continue;
                }
                node.add(new DefaultMutableTreeNode(new FileTreeNode(entry)));
                hasMatches = true;
            }
        }

        if (!isRoot && !hasMatches) {
            return null;
        }

        return node;
    }

    private void expandAllRows() {
        for (int i = 0; i < fileTree.getRowCount(); i++) {
            fileTree.expandRow(i);
        }
    }

    private void openFileInTab(File file) {
        // If already open, focus
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            Component c = tabbedPane.getComponentAt(i);
            if (c instanceof JScrollPane) {
                JScrollPane sp = (JScrollPane) c;
                JViewport v = sp.getViewport();
                Component inner = v.getView();
                if (inner instanceof JPanel) {
                    JPanel p = (JPanel) inner;
                    if (file.equals(p.getClientProperty("file"))) {
                        tabbedPane.setSelectedIndex(i);
                        return;
                    }
                }
            }
        }

        try {
            String content = readFile(file, Charset.forName("UTF-8"));
            JPanel viewer = makeViewerPanel(file, content);
            tabbedPane.addTab(file.getName(), viewer);
            int idx = tabbedPane.indexOfComponent(viewer);
            tabbedPane.setTabComponentAt(idx, makeTabTitle(file.getName()));
            tabbedPane.setToolTipTextAt(idx, file.getAbsolutePath());
            tabbedPane.setSelectedIndex(idx);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Failed to read file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Component makeTabTitle(String title) {
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT, scale(4), 0));
        pnl.setOpaque(false);
        pnl.setBorder(new EmptyBorder(0, 0, 0, scale(8)));
        JLabel lbl = new JLabel(title);
        lbl.setFont(UI_FONT.deriveFont(Font.BOLD, (float) UI_FONT.getSize()));
        JButton close = new JButton("x");
        close.setFont(UI_FONT);
        close.setForeground(new Color(0x5b6370));
        close.setBackground(new Color(0xf0f2f8));
        close.setBorder(new LineBorder(new Color(0xd5d9e0), 1, true));
        close.setMargin(new Insets(scale(2), scale(6), scale(2), scale(6)));
        close.setFocusable(false);
        close.setContentAreaFilled(true);
        close.setOpaque(true);
        close.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        close.addActionListener(e -> {
            int index = tabbedPane.indexOfTabComponent(pnl);
            if (index >= 0) {
                tabbedPane.removeTabAt(index);
            }
        });
        close.getModel().addChangeListener(evt -> {
            ButtonModel model = close.getModel();
            if (!model.isEnabled()) {
                close.setBackground(new Color(0xe1e3ea));
            } else if (model.isPressed()) {
                close.setBackground(new Color(0xd6dae4));
            } else if (model.isRollover()) {
                close.setBackground(new Color(0xe6e9f2));
            } else {
                close.setBackground(new Color(0xf0f2f8));
            }
        });
        pnl.add(lbl);
        pnl.add(close);
        return pnl;
    }

    private JPanel makeViewerPanel(File file, String content) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.putClientProperty("file", file);

        JTextPane textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setFont(MONO_FONT);
        textPane.setBackground(SURFACE_COLOR);
        StyledDocument doc = textPane.getStyledDocument();
        applyBasicStyles(doc);
        // Insert content with styles
        try {
            doc.insertString(0, content, doc.getStyle("default"));
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
        // Run simple syntax highlight
        doSyntaxHighlight(textPane, content);

        String primaryTypeName = detectPrimaryTypeName(content, file != null ? file.getName() : null);
        List<MethodInfo> methodInfos = parseMethodInfos(content, primaryTypeName);
        List<MethodInfo> inheritedMethods = loadInheritedMethods(primaryTypeName, content);
        List<MethodInfo> allMethods = new ArrayList<>(methodInfos);
        allMethods.addAll(inheritedMethods);
        JEditorPane methodInfoPane = createMethodInfoPane();
        updateMethodInfoDisplay(methodInfoPane, null);
        attachMethodInsight(textPane, methodInfoPane, allMethods, content, primaryTypeName);

        // Line numbers
        JTextArea lineNumbers = new JTextArea();
        lineNumbers.setEditable(false);
        lineNumbers.setFont(textPane.getFont());
        lineNumbers.setBackground(new Color(0xf1f3f4));
        lineNumbers.setForeground(TEXT_MUTED);
        updateLineNumbers(lineNumbers, content);

        // Sync scrolling
        JScrollPane scroll = new JScrollPane(textPane);
        scroll.setBorder(new MatteBorder(1, 0, 0, 0, BORDER_COLOR));
        scroll.setBackground(SURFACE_COLOR);
        scroll.setRowHeaderView(lineNumbers);
        panel.add(scroll, BorderLayout.CENTER);

        panel.add(makeInsightContainer(methodInfoPane), BorderLayout.SOUTH);

        return panel;
    }

    private void updateLineNumbers(JTextArea numbers, String content) {
        int lines = content.split("\n", -1).length;
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= lines; i++) {
            sb.append(i).append(System.lineSeparator());
        }
        numbers.setText(sb.toString());
    }

    private void applyBasicStyles(StyledDocument doc) {
        Style defaultStyle = doc.addStyle("default", null);
        StyleConstants.setForeground(defaultStyle, new Color(0x24292e));
        StyleConstants.setFontFamily(defaultStyle, Font.MONOSPACED);
        StyleConstants.setFontSize(defaultStyle, scaledFontSize(BASE_FONT_SIZE));

        Style keyword = doc.addStyle("keyword", null);
        StyleConstants.setForeground(keyword, new Color(0x7c4dff)); // purple
        StyleConstants.setBold(keyword, true);

        Style comment = doc.addStyle("comment", null);
        StyleConstants.setForeground(comment, new Color(0x5c7c6c)); // desaturated green
        StyleConstants.setItalic(comment, true);

        Style string = doc.addStyle("string", null);
        StyleConstants.setForeground(string, new Color(0xf28b54)); // orange

        Style number = doc.addStyle("number", null);
        StyleConstants.setForeground(number, new Color(0x1a76d2)); // blue

        Style annotation = doc.addStyle("annotation", null);
        StyleConstants.setForeground(annotation, new Color(0x009688)); // teal

        Style className = doc.addStyle("class-name", null);
        StyleConstants.setForeground(className, new Color(0x1976d2)); // blue
        StyleConstants.setBold(className, true);

        Style method = doc.addStyle("method", null);
        StyleConstants.setForeground(method, new Color(0x673ab7)); // deep purple

        Style field = doc.addStyle("field", null);
        StyleConstants.setForeground(field, new Color(0x00796b)); // teal
    }

    private void doSyntaxHighlight(JTextPane pane, String content) {
        // Simple regex-based highlighter. Not incremental.
        String[] keywords = new String[]{
            "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue",
            "default", "double", "do", "else", "enum", "extends", "final", "finally", "float", "for", "goto", "if", "implements",
            "import", "instanceof", "int", "interface", "long", "native", "new", "package", "private", "protected", "public", "return",
            "short", "static", "strictfp", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while"
        };
        Set<String> kws = new HashSet<>(Arrays.asList(keywords));

        StyledDocument doc = pane.getStyledDocument();
        // First set entire text to default
        doc.setCharacterAttributes(0, content.length(), doc.getStyle("default"), true);

        // Comments (//... and /* ... */)
        Pattern lineComment = Pattern.compile("//.*?$", Pattern.MULTILINE);
        Pattern blockComment = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);
        Matcher m = blockComment.matcher(content);
        while (m.find()) {
            doc.setCharacterAttributes(m.start(), m.end() - m.start(), doc.getStyle("comment"), false);
        }
        m = lineComment.matcher(content);
        while (m.find()) {
            doc.setCharacterAttributes(m.start(), m.end() - m.start(), doc.getStyle("comment"), false);
        }

        // Strings
        Pattern stringPat = Pattern.compile("\"(?:\\\\.|[^\\\"])*\"|\'(?:\\\\.|[^\\\'])*\'");
        m = stringPat.matcher(content);
        while (m.find()) {
            doc.setCharacterAttributes(m.start(), m.end() - m.start(), doc.getStyle("string"), false);
        }

        // Numbers
        Pattern numberPat = Pattern.compile("\\b\\d+(?:\\.\\d+)?\\b");
        m = numberPat.matcher(content);
        while (m.find()) {
            doc.setCharacterAttributes(m.start(), m.end() - m.start(), doc.getStyle("number"), false);
        }

        // Annotations
        Pattern annotationPat = Pattern.compile("@[A-Za-z_][A-Za-z0-9_]*");
        m = annotationPat.matcher(content);
        while (m.find()) {
            doc.setCharacterAttributes(m.start(), m.end() - m.start(), doc.getStyle("annotation"), false);
        }

        // Class/interface/enum names after keywords
        Pattern classPat = Pattern.compile("\\b(class|interface|enum)\\s+([A-Za-z_][A-Za-z0-9_]*)");
        m = classPat.matcher(content);
        while (m.find()) {
            doc.setCharacterAttributes(m.start(2), m.group(2).length(), doc.getStyle("class-name"), false);
        }

        // Fields: simple heuristic for 'type name;' or 'type name ='
        Pattern fieldPat = Pattern.compile("\\b([A-Za-z_][A-Za-z0-9_<>\\[\\]]*)\\s+([A-Za-z_][A-Za-z0-9_]*)\\s*(=|;)");
        m = fieldPat.matcher(content);
        while (m.find()) {
            doc.setCharacterAttributes(m.start(2), m.group(2).length(), doc.getStyle("field"), false);
        }

        // Methods: identifier followed by '(' not preceded by 'new' or keyword
        Pattern methodPat = Pattern.compile("\\b([A-Za-z_][A-Za-z0-9_]*)\\s*\\(");
        m = methodPat.matcher(content);
        while (m.find()) {
            String name = m.group(1);
            // Skip keywords and control flow statements
            if (kws.contains(name) || "if".equals(name) || "for".equals(name) || "while".equals(name)
                    || "switch".equals(name) || "catch".equals(name) || "return".equals(name) || "new".equals(name)) {
                continue;
            }
            doc.setCharacterAttributes(m.start(1), name.length(), doc.getStyle("method"), false);
        }

        // Keywords (only outside strings and comments ideally, but we'll do a simple approach)
        Pattern word = Pattern.compile("\\b([A-Za-z_][A-Za-z0-9_]*)\\b");
        m = word.matcher(content);
        while (m.find()) {
            String w = m.group(1);
            if (kws.contains(w)) {
                doc.setCharacterAttributes(m.start(1), w.length(), doc.getStyle("keyword"), false);
            }
        }
    }

    private void attachMethodInsight(JTextPane textPane, JEditorPane infoPane, List<MethodInfo> methods, String fullText, String primaryTypeName) {
        textPane.addCaretListener(e -> {
            MethodInfo hit = resolveInsight(textPane, methods, fullText, primaryTypeName, e.getDot());
            updateMethodInfoDisplay(infoPane, hit);
        });
        textPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int pos = textPane.viewToModel(e.getPoint());
                if (pos < 0) {
                    return;
                }
                MethodInfo hit = resolveInsight(textPane, methods, fullText, primaryTypeName, pos);
                updateMethodInfoDisplay(infoPane, hit);
            }
        });
        MethodInfo initial = resolveInsight(textPane, methods, fullText, primaryTypeName, textPane.getCaretPosition());
        updateMethodInfoDisplay(infoPane, initial);
        installDocHoverHints(textPane, methods, fullText, primaryTypeName);
    }

    private void installDocHoverHints(JTextPane textPane, List<MethodInfo> methods, String fullText, String primaryTypeName) {
        Highlighter highlighter = textPane.getHighlighter();
        Cursor defaultCursor = textPane.getCursor();
        MouseInputAdapter hoverListener = new MouseInputAdapter() {
            private Object highlightTag;
            private int lastStart = -1;
            private int lastEnd = -1;

            @Override
            public void mouseMoved(MouseEvent e) {
                updateHover(e);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                clearHover();
            }

            private void updateHover(MouseEvent e) {
                int pos = textPane.viewToModel(e.getPoint());
                if (pos < 0) {
                    clearHover();
                    return;
                }
                MethodInfo hit = resolveInsight(textPane, methods, fullText, primaryTypeName, pos);
                if (hit == null) {
                    clearHover();
                    return;
                }
                WordMatch match = extractWordAt(fullText, pos);
                if (match == null || match.end <= match.start) {
                    clearHover();
                    return;
                }
                applyHighlight(match.start, match.end);
                textPane.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            private void applyHighlight(int start, int end) {
                if (start == lastStart && end == lastEnd) {
                    return;
                }
                removeHighlight();
                try {
                    highlightTag = highlighter.addHighlight(start, end, DOC_HOVER_PAINTER);
                    lastStart = start;
                    lastEnd = end;
                } catch (BadLocationException ex) {
                    lastStart = -1;
                    lastEnd = -1;
                }
            }

            private void removeHighlight() {
                if (highlightTag != null) {
                    highlighter.removeHighlight(highlightTag);
                    highlightTag = null;
                }
                lastStart = -1;
                lastEnd = -1;
            }

            private void clearHover() {
                removeHighlight();
                textPane.setCursor(defaultCursor);
            }
        };
        textPane.addMouseMotionListener(hoverListener);
        textPane.addMouseListener(hoverListener);
    }

    private JPanel makeInsightContainer(JEditorPane infoPane) {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(new Color(0xf8faff));
        container.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, BORDER_COLOR),
                new EmptyBorder(scale(6), scale(12), scale(6), scale(12))
        ));
        JScrollPane infoScroll = new JScrollPane(infoPane);
        infoScroll.setBorder(null);
        infoScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        infoScroll.setPreferredSize(new Dimension(10, scale(160)));
        container.add(infoScroll, BorderLayout.CENTER);
        return container;
    }

    private MethodInfo resolveInsight(JTextPane textPane, List<MethodInfo> methods, String fullText, String primaryTypeName, int caretPos) {
        MethodInfo invocation = inferInvocationAt(fullText, caretPos, primaryTypeName);
        MethodInfo matchedInvocation = matchMethodDefinition(invocation, methods);
        if (matchedInvocation != null) {
            return matchedInvocation;
        }
        if (invocation != null) {
            return invocation;
        }

        MethodInfo targetedMethod = findMethodAt(methods, caretPos);
        if (targetedMethod != null) {
            return targetedMethod;
        }

        int selStart = textPane.getSelectionStart();
        int selEnd = textPane.getSelectionEnd();
        if (selEnd > selStart) {
            MethodInfo bySelection = inferInvocationAt(fullText, Math.max(selEnd - 1, selStart), primaryTypeName);
            MethodInfo matchedSelection = matchMethodDefinition(bySelection, methods);
            if (matchedSelection != null) {
                return matchedSelection;
            }
            if (bySelection == null) {
                bySelection = inferInvocationAt(fullText, selStart, primaryTypeName);
            }
            matchedSelection = matchMethodDefinition(bySelection, methods);
            if (matchedSelection != null) {
                return matchedSelection;
            }
            if (bySelection == null) {
                bySelection = inferByName(fullText, selEnd - 1, methods, primaryTypeName);
            }
            if (bySelection == null) {
                bySelection = inferByName(fullText, selStart, methods, primaryTypeName);
            }
            if (bySelection != null) {
                return bySelection;
            }
        }

        MethodInfo byCaret = inferByName(fullText, caretPos, methods, primaryTypeName);
        if (byCaret != null) {
            return byCaret;
        }

        return null;
    }

    private JEditorPane createMethodInfoPane() {
        JEditorPane infoPane = new JEditorPane();
        infoPane.setEditable(false);
        infoPane.setContentType("text/html");
        infoPane.setBackground(new Color(0xf8faff));
        infoPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        infoPane.setFont(UI_FONT);
        infoPane.setText(buildDefaultInsightMessage());
        return infoPane;
    }

    private MethodInfo matchMethodDefinition(MethodInfo candidate, List<MethodInfo> methods) {
        if (candidate == null) {
            return null;
        }
        String candidateOwner = canonicalOwnerKey(candidate.ownerType);
        for (MethodInfo info : methods) {
            if (!info.name.equals(candidate.name)) {
                continue;
            }
            String infoOwner = canonicalOwnerKey(info.ownerType);
            if (!candidateOwner.isEmpty() && !infoOwner.isEmpty() && !candidateOwner.equals(infoOwner)) {
                continue;
            }
            if (info.params.size() == candidate.params.size()) {
                return info;
            }
        }
        return null;
    }

    private MethodInfo inferByName(String fullText, int anchorPos, List<MethodInfo> knownMethods, String primaryTypeName) {
        WordMatch match = extractWordAt(fullText, anchorPos);
        if (match == null) {
            return null;
        }
        for (MethodInfo info : knownMethods) {
            if (info.name.equals(match.word)) {
                return info;
            }
        }
        String owner = detectOwnerBefore(fullText, match.start, primaryTypeName);
        MethodInfo synthetic = new MethodInfo(match.word, "", "", "", "", Collections.emptyList(), match.start, match.end, computeLineNumber(fullText, match.start), owner, false, false);
        String builtin = builtInApiSummary(synthetic);
        if (builtin != null) {
            return synthetic;
        }
        String summary = generateMethodSummary(synthetic);
        if (!GENERIC_SUMMARY.equals(summary)) {
            return synthetic;
        }
        return null;
    }

    private void updateMethodInfoDisplay(JEditorPane pane, MethodInfo info) {
        if (info == null) {
            pane.setText(buildDefaultInsightMessage());
        } else {
            pane.setText(buildMethodExplanation(info));
        }
        pane.setCaretPosition(0);
    }

    private MethodInfo findMethodAt(List<MethodInfo> methods, int offset) {
        for (MethodInfo info : methods) {
            if (offset >= info.startOffset && offset <= info.endOffset) {
                return info;
            }
        }
        return null;
    }

    private String buildDefaultInsightMessage() {
        return "<html><body style='margin:0;font-family:" + htmlEscape(UI_FONT.getFamily())
                + ";font-size:" + UI_FONT.getSize() + "px;color:#4a4f57;'>Chọn một phương thức trong tệp để xem phần giải thích bằng tiếng Việt.</body></html>";
    }

    private List<MethodInfo> parseMethodInfos(String content, String primaryTypeName) {
        List<MethodInfo> methods = new ArrayList<>();
        Pattern methodPattern = Pattern.compile(
                "(?m)^[\\t ]*(?:@\\w+(?:\\([^)]*\\))?\\s*)*"
                + "((?:public|protected|private|static|final|abstract|synchronized|native|strictfp|default|\\\\n|\\\\r|\\s)+)?"
                + "(?:<[^>]+>\\s*)?"
                + "([\\w$\\[\\].<>?,\\s]+?)\\s+"
                + "([\\w$]+)\\s*\\(([^)]*)\\)\\s*(throws[^{]*)?\\{"
        );
        Matcher matcher = methodPattern.matcher(content);
        while (matcher.find()) {
            int bodyStartBrace = matcher.end() - 1;
            int bodyEnd = findMatchingBrace(content, bodyStartBrace);
            if (bodyEnd < 0) {
                continue;
            }
            MethodInfo info = buildMethodInfo(matcher, content, bodyStartBrace, bodyEnd, primaryTypeName, false);
            if (info != null) {
                methods.add(info);
            }
        }

        if (primaryTypeName != null && !primaryTypeName.isEmpty()) {
            Pattern ctorPattern = Pattern.compile(
                    "(?m)^[\\t ]*(?:@\\w+(?:\\([^)]*\\))?\\s*)*"
                    + "((?:public|protected|private|static|final|synchronized|native|strictfp|default|\\\\n|\\\\r|\\s)+)?"
                    + "(?:<[^>]+>\\s*)?"
                    + Pattern.quote(primaryTypeName) + "\\s*\\(([^)]*)\\)\\s*(throws[^{]*)?\\{"
            );
            Matcher ctorMatcher = ctorPattern.matcher(content);
            while (ctorMatcher.find()) {
                int bodyStartBrace = ctorMatcher.end() - 1;
                int bodyEnd = findMatchingBrace(content, bodyStartBrace);
                if (bodyEnd < 0) {
                    continue;
                }
                MethodInfo info = buildConstructorInfo(ctorMatcher, content, bodyStartBrace, bodyEnd, primaryTypeName);
                if (info != null) {
                    methods.add(info);
                }
            }
        }

        methods.sort(Comparator.comparingInt(m -> m.startOffset));
        return methods;
    }

    private List<MethodInfo> loadInheritedMethods(String primaryTypeName, String content) {
        if (primaryTypeName == null || primaryTypeName.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> parents = extractParentTypes(content);
        if (parents.isEmpty()) {
            return Collections.emptyList();
        }
        List<MethodInfo> inherited = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        visited.add(primaryTypeName);
        for (String parent : parents) {
            inherited.addAll(resolveInheritanceChain(parent, visited));
        }
        return inherited;
    }

    private MethodInfo buildMethodInfo(Matcher matcher, String content, int bodyStart, int bodyEnd, String ownerType, boolean constructor) {
        String access = normalizeSpace(groupSafely(matcher, 1));
        String modifiers = normalizeSpace(groupSafely(matcher, 2));
        String returnType = normalizeSpace(groupSafely(matcher, 3));
        String name = groupSafely(matcher, 4).trim();
        String params = groupSafely(matcher, 5);
        String throwsClause = normalizeSpace(groupSafely(matcher, 6));
        int startOffset = matcher.start();
        int lineNumber = computeLineNumber(content, startOffset);
        List<ParamInfo> paramInfos = parseParameters(params);
        return new MethodInfo(name, returnType, access, modifiers, throwsClause, paramInfos, startOffset, bodyEnd, lineNumber, ownerType, constructor, false);
    }

    private MethodInfo buildConstructorInfo(Matcher matcher, String content, int bodyStart, int bodyEnd, String ownerType) {
        String access = normalizeSpace(groupSafely(matcher, 1));
        String modifiers = normalizeSpace(groupSafely(matcher, 2));
        String params = groupSafely(matcher, 3);
        String throwsClause = normalizeSpace(groupSafely(matcher, 4));
        int startOffset = matcher.start();
        int lineNumber = computeLineNumber(content, startOffset);
        List<ParamInfo> paramInfos = parseParameters(params);
        return new MethodInfo(ownerType, ownerType, access, modifiers, throwsClause, paramInfos, startOffset, bodyEnd, lineNumber, ownerType, true, false);
    }

    private int findMatchingBrace(String text, int openPos) {
        int depth = 0;
        for (int i = openPos; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '{') {
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0) {
                    return i;
                }
            } else if (c == '"' || c == '\'') {
                i = skipQuoted(text, i, c);
            } else if (c == '/' && i + 1 < text.length()) {
                char next = text.charAt(i + 1);
                if (next == '/') {
                    i = skipLineComment(text, i + 2);
                } else if (next == '*') {
                    i = skipBlockComment(text, i + 2);
                }
            }
        }
        return -1;
    }

    private int skipQuoted(String text, int start, char quote) {
        for (int i = start + 1; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\\') {
                i++;
            } else if (c == quote) {
                return i;
            }
        }
        return text.length() - 1;
    }

    private int skipLineComment(String text, int start) {
        for (int i = start; i < text.length(); i++) {
            if (text.charAt(i) == '\n') {
                return i;
            }
        }
        return text.length() - 1;
    }

    private int skipBlockComment(String text, int start) {
        for (int i = start; i < text.length() - 1; i++) {
            if (text.charAt(i) == '*' && text.charAt(i + 1) == '/') {
                return i + 1;
            }
        }
        return text.length() - 1;
    }

    private List<ParamInfo> parseParameters(String params) {
        List<ParamInfo> result = new ArrayList<>();
        if (params == null || params.trim().isEmpty()) {
            return result;
        }
        String[] parts = params.split(",");
        for (String raw : parts) {
            String trimmed = raw.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            String[] tokens = trimmed.split("\\s+");
            if (tokens.length == 0) {
                continue;
            }
            String name = tokens[tokens.length - 1];
            StringBuilder typeBuilder = new StringBuilder();
            for (int i = 0; i < tokens.length - 1; i++) {
                if (i > 0) {
                    typeBuilder.append(' ');
                }
                typeBuilder.append(tokens[i]);
            }
            String type = typeBuilder.length() > 0 ? typeBuilder.toString() : name;
            if (type.endsWith("...")) {
                type = type.replace("...", "...");
            }
            result.add(new ParamInfo(type, name));
        }
        return result;
    }

    private List<ParamInfo> buildArgumentInfo(String argsText) {
        List<ParamInfo> result = new ArrayList<>();
        List<String> parts = splitArguments(argsText);
        int idx = 1;
        for (String part : parts) {
            String expr = part.trim();
            if (expr.isEmpty()) {
                continue;
            }
            result.add(new ParamInfo("Biểu thức", "arg" + idx++ + " = " + (expr.length() > 40 ? expr.substring(0, 37) + "..." : expr)));
        }
        return result;
    }

    private List<String> splitArguments(String text) {
        List<String> args = new ArrayList<>();
        if (text == null) {
            return args;
        }
        StringBuilder current = new StringBuilder();
        int depthParen = 0;
        int depthBrace = 0;
        int depthBracket = 0;
        boolean inString = false;
        char stringChar = 0;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (inString) {
                current.append(ch);
                if (ch == stringChar && (i == 0 || text.charAt(i - 1) != '\\')) {
                    inString = false;
                }
                continue;
            }
            switch (ch) {
                case '\'':
                case '"':
                    inString = true;
                    stringChar = ch;
                    current.append(ch);
                    break;
                case '(':
                    depthParen++;
                    current.append(ch);
                    break;
                case ')':
                    depthParen = Math.max(0, depthParen - 1);
                    current.append(ch);
                    break;
                case '[':
                    depthBracket++;
                    current.append(ch);
                    break;
                case ']':
                    depthBracket = Math.max(0, depthBracket - 1);
                    current.append(ch);
                    break;
                case '{':
                    depthBrace++;
                    current.append(ch);
                    break;
                case '}':
                    depthBrace = Math.max(0, depthBrace - 1);
                    current.append(ch);
                    break;
                case ',':
                    if (depthParen == 0 && depthBrace == 0 && depthBracket == 0) {
                        args.add(current.toString());
                        current.setLength(0);
                    } else {
                        current.append(ch);
                    }
                    break;
                default:
                    current.append(ch);
            }
        }
        if (current.length() > 0) {
            args.add(current.toString());
        }
        return args;
    }

    private List<MethodInfo> resolveInheritanceChain(String typeName, Set<String> visited) {
        String clean = cleanTypeName(typeName);
        if (clean.isEmpty() || visited.contains(clean)) {
            return Collections.emptyList();
        }
        Set<String> newVisited = new HashSet<>(visited);
        newVisited.add(clean);

        String canonical = canonicalOwnerKey(clean);
        if (BUILTIN_DOCS.containsKey(canonical)) {
            inheritanceCache.put(clean, Collections.emptyList());
            return Collections.emptyList();
        }

        if (inheritanceCache.containsKey(clean)) {
            return new ArrayList<>(inheritanceCache.get(clean));
        }

        File classFile = findJavaFileForClass(clean);
        if (classFile == null) {
            inheritanceCache.put(clean, Collections.emptyList());
            return Collections.emptyList();
        }

        String content;
        try {
            content = readFile(classFile, Charset.forName("UTF-8"));
        } catch (IOException e) {
            inheritanceCache.put(clean, Collections.emptyList());
            return Collections.emptyList();
        }

        String baseType = detectPrimaryTypeName(content, classFile.getName());
        List<MethodInfo> methods = parseMethodInfos(content, baseType);
        List<MethodInfo> inherited = new ArrayList<>();
        for (MethodInfo info : methods) {
            inherited.add(info.asInherited(baseType));
        }

        for (String parent : extractParentTypes(content)) {
            inherited.addAll(resolveInheritanceChain(parent, newVisited));
        }

        inheritanceCache.put(clean, new ArrayList<>(inherited));
        return inherited;
    }

    private List<String> extractParentTypes(String content) {
        List<String> parents = new ArrayList<>();
        Set<String> seen = new LinkedHashSet<>();
        if (content == null || content.isEmpty()) {
            return parents;
        }

        Pattern classPattern = Pattern.compile("class\\s+\\w+\\s*(?:extends\\s+([\\w$.<>]+))?\\s*(?:implements\\s+([\\w$.,<>\\s]+))?", Pattern.MULTILINE);
        Matcher matcher = classPattern.matcher(content);
        if (matcher.find()) {
            String extendType = matcher.group(1);
            if (extendType != null && !extendType.trim().isEmpty()) {
                String clean = cleanTypeName(extendType);
                if (!clean.isEmpty() && seen.add(clean)) {
                    parents.add(clean);
                }
            }
            String impl = matcher.group(2);
            if (impl != null && !impl.trim().isEmpty()) {
                for (String part : impl.split(",")) {
                    String clean = cleanTypeName(part);
                    if (!clean.isEmpty() && seen.add(clean)) {
                        parents.add(clean);
                    }
                }
            }
        }

        Pattern interfacePattern = Pattern.compile("interface\\s+\\w+\\s+extends\\s+([\\w$.,<>\\s]+)", Pattern.MULTILINE);
        Matcher interfaceMatcher = interfacePattern.matcher(content);
        while (interfaceMatcher.find()) {
            String list = interfaceMatcher.group(1);
            if (list != null) {
                for (String part : list.split(",")) {
                    String clean = cleanTypeName(part);
                    if (!clean.isEmpty() && seen.add(clean)) {
                        parents.add(clean);
                    }
                }
            }
        }

        return parents;
    }

    private String cleanTypeName(String raw) {
        if (raw == null) {
            return "";
        }
        String trimmed = raw.trim();
        if (trimmed.isEmpty()) {
            return "";
        }
        int generic = trimmed.indexOf('<');
        if (generic >= 0) {
            trimmed = trimmed.substring(0, generic);
        }
        while (trimmed.endsWith("[]")) {
            trimmed = trimmed.substring(0, trimmed.length() - 2);
        }
        int space = trimmed.indexOf(' ');
        if (space >= 0) {
            trimmed = trimmed.substring(0, space);
        }
        trimmed = trimmed.replaceAll("[;{}]", "");
        return simplifyOwnerName(trimmed);
    }

    private File findJavaFileForClass(String simpleName) {
        if (simpleName == null || simpleName.isEmpty()) {
            return null;
        }
        if (classFileCache.containsKey(simpleName)) {
            return classFileCache.get(simpleName);
        }
        if (currentRoot == null) {
            classFileCache.put(simpleName, null);
            return null;
        }
        try {
            Path rootPath = currentRoot.toPath();
            try ( java.util.stream.Stream<Path> stream = Files.walk(rootPath)) {
                Optional<Path> match = stream
                        .filter(p -> p.getFileName().toString().equals(simpleName + ".java"))
                        .findFirst();
                File file = match.map(Path::toFile).orElse(null);
                classFileCache.put(simpleName, file);
                return file;
            }
        } catch (IOException e) {
            classFileCache.put(simpleName, null);
            return null;
        }
    }

    private String findTypeOfIdentifier(String content, String identifier, int beforePos) {
        if (content == null || identifier == null || identifier.isEmpty()) {
            return "";
        }
        int searchEnd = beforePos;
        if (searchEnd < 0 || searchEnd > content.length()) {
            searchEnd = content.length();
        }
        String prefix = content.substring(0, searchEnd);
        String type = "";

        Pattern declPattern = Pattern.compile("([\\w$\\[\\].<>?,\\s]+?)\\s+" + Pattern.quote(identifier) + "\\s*(?:=|;|,|\\))", Pattern.MULTILINE);
        Matcher matcher = declPattern.matcher(prefix);
        while (matcher.find()) {
            type = convertTypeForDisplay(matcher.group(1));
        }

        Pattern enhancedFor = Pattern.compile("for\\s*\\(\\s*([\\w$\\[\\].<>?,\\s]+?)\\s+" + Pattern.quote(identifier) + "\\s*:");
        matcher = enhancedFor.matcher(prefix);
        while (matcher.find()) {
            type = convertTypeForDisplay(matcher.group(1));
        }

        Pattern fieldPattern = Pattern.compile("(?:private|protected|public|static|final|transient|volatile|\\s)+([\\w$\\[\\].<>?,\\s]+?)\\s+" + Pattern.quote(identifier) + "\\s*(?:=|;|,)", Pattern.MULTILINE);
        matcher = fieldPattern.matcher(prefix);
        while (matcher.find()) {
            type = convertTypeForDisplay(matcher.group(1));
        }

        return type;
    }

    private String resolveOwnerType(String ownerToken, String content, int referencePos, String primaryTypeName) {
        if (ownerToken == null) {
            ownerToken = "";
        }
        String raw = ownerToken.trim();
        if (raw.isEmpty()) {
            return primaryTypeName != null ? primaryTypeName : "";
        }

        String lowerRaw = raw.toLowerCase(Locale.ROOT);
        if (lowerRaw.contains("system.out")) {
            return "PrintStream";
        }
        if (lowerRaw.contains("system.err")) {
            return "PrintStream";
        }

        String simple = simplifyOwnerName(raw);
        String lowerSimple = simple.toLowerCase(Locale.ROOT);
        if ("this".equals(lowerSimple) || "super".equals(lowerSimple)) {
            return primaryTypeName != null ? primaryTypeName : "";
        }
        if ("system".equals(lowerSimple)) {
            return "System";
        }
        if ("out".equals(lowerSimple) || "err".equals(lowerSimple)) {
            return "PrintStream";
        }

        String declared = findTypeOfIdentifier(content, simple, referencePos);
        if (!declared.isEmpty()) {
            return declared;
        }

        // Try again with last segment if ownerToken chá»©a dáº¥u cháº¥m
        int dot = simple.lastIndexOf('.');
        if (dot >= 0 && dot < simple.length() - 1) {
            String lastSegment = simple.substring(dot + 1);
            declared = findTypeOfIdentifier(content, lastSegment, referencePos);
            if (!declared.isEmpty()) {
                return declared;
            }
        }

        if (primaryTypeName != null && primaryTypeName.equals(simple)) {
            return primaryTypeName;
        }

        return simple;
    }

    private WordMatch extractWordAt(String text, int anchor) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        if (anchor < 0) {
            anchor = 0;
        }
        if (anchor >= text.length()) {
            anchor = text.length() - 1;
        }

        int pos = anchor;
        if (!isIdentifierPart(text.charAt(pos))) {
            int left = pos - 1;
            while (left >= 0 && !isIdentifierPart(text.charAt(left))) {
                left--;
            }
            if (left >= 0) {
                pos = left;
            } else {
                int right = anchor + 1;
                while (right < text.length() && !isIdentifierPart(text.charAt(right))) {
                    right++;
                }
                if (right >= text.length()) {
                    return null;
                }
                pos = right;
            }
        }

        int start = pos;
        while (start > 0 && isIdentifierPart(text.charAt(start - 1))) {
            start--;
        }
        int end = pos + 1;
        while (end < text.length() && isIdentifierPart(text.charAt(end))) {
            end++;
        }
        if (start >= end) {
            return null;
        }
        return new WordMatch(text.substring(start, end), start, end);
    }

    private String detectOwnerBefore(String text, int position, String primaryTypeName) {
        if (text == null || text.isEmpty() || position <= 0) {
            return "";
        }
        int index = position - 1;
        while (index >= 0 && Character.isWhitespace(text.charAt(index))) {
            index--;
        }
        if (index < 0 || text.charAt(index) != '.') {
            return "";
        }
        int ownerEnd = index - 1;
        while (ownerEnd >= 0 && Character.isWhitespace(text.charAt(ownerEnd))) {
            ownerEnd--;
        }
        if (ownerEnd < 0) {
            return "";
        }
        int ownerStart = ownerEnd;
        while (ownerStart >= 0 && (isIdentifierPart(text.charAt(ownerStart)) || text.charAt(ownerStart) == '.')) {
            ownerStart--;
        }
        return resolveOwnerType(text.substring(ownerStart + 1, ownerEnd + 1), text, ownerStart, primaryTypeName);
    }

    private MethodInfo inferInvocationAt(String content, int offset, String primaryTypeName) {
        if (content == null || content.isEmpty()) {
            return null;
        }
        if (offset < 0) {
            offset = 0;
        }
        if (offset >= content.length()) {
            offset = content.length() - 1;
        }

        int openParen = detectCallParenForward(content, offset);
        if (openParen < 0) {
            openParen = findCallOpenParen(content, offset);
        }
        if (openParen < 0) {
            return null;
        }
        int closeParen = findMatchingParenForward(content, openParen);
        if (closeParen < 0) {
            return null;
        }

        int nameEnd = openParen - 1;
        while (nameEnd >= 0 && Character.isWhitespace(content.charAt(nameEnd))) {
            nameEnd--;
        }
        if (nameEnd < 0) {
            return null;
        }
        int nameStart = nameEnd;
        while (nameStart >= 0 && isIdentifierPart(content.charAt(nameStart))) {
            nameStart--;
        }
        String name = content.substring(nameStart + 1, nameEnd + 1);
        if (name.isEmpty()) {
            return null;
        }

        int ownerSearch = nameStart;
        while (ownerSearch >= 0 && Character.isWhitespace(content.charAt(ownerSearch))) {
            ownerSearch--;
        }
        String ownerToken = "";
        if (ownerSearch >= 0 && content.charAt(ownerSearch) == '.') {
            int ownerEnd = ownerSearch - 1;
            while (ownerEnd >= 0 && Character.isWhitespace(content.charAt(ownerEnd))) {
                ownerEnd--;
            }
            int ownerStart = ownerEnd;
            while (ownerStart >= 0 && (isIdentifierPart(content.charAt(ownerStart)) || content.charAt(ownerStart) == '.')) {
                ownerStart--;
            }
            ownerToken = content.substring(ownerStart + 1, ownerEnd + 1);
        }
        String ownerType = resolveOwnerType(ownerToken, content, Math.max(0, nameStart), primaryTypeName);
        if (ownerType.isEmpty()) {
            ownerType = simplifyOwnerName(ownerToken);
        }

        String argsText = content.substring(openParen + 1, closeParen);
        List<ParamInfo> params = buildArgumentInfo(argsText);
        int startOffset = Math.max(0, nameStart + 1);
        int endOffset = closeParen;
        int line = computeLineNumber(content, startOffset);
        return new MethodInfo(name, "", "", "", "", params, startOffset, endOffset, line, ownerType, false, false);
    }

    private int detectCallParenForward(String text, int offset) {
        WordMatch word = extractWordAt(text, offset);
        if (word == null || word.end >= text.length()) {
            return -1;
        }
        int idx = word.end;
        while (idx < text.length() && Character.isWhitespace(text.charAt(idx))) {
            idx++;
        }
        if (idx < text.length() && text.charAt(idx) == '(') {
            return idx;
        }
        return -1;
    }

    private int findCallOpenParen(String text, int offset) {
        int depth = 0;
        boolean inString = false;
        char stringChar = 0;
        for (int i = Math.min(offset, text.length() - 1); i >= 0; i--) {
            char ch = text.charAt(i);
            if (inString) {
                if (ch == stringChar && (i == 0 || text.charAt(i - 1) != '\\')) {
                    inString = false;
                }
                continue;
            }
            if (ch == '"' || ch == '\'') {
                inString = true;
                stringChar = ch;
                continue;
            }
            if (ch == ')') {
                depth++;
                continue;
            }
            if (ch == '(') {
                if (depth > 0) {
                    depth--;
                    continue;
                }
                int nameEnd = i - 1;
                while (nameEnd >= 0 && Character.isWhitespace(text.charAt(nameEnd))) {
                    nameEnd--;
                }
                if (nameEnd < 0) {
                    return -1;
                }
                int nameStart = nameEnd;
                while (nameStart >= 0 && isIdentifierPart(text.charAt(nameStart))) {
                    nameStart--;
                }
                String candidate = text.substring(nameStart + 1, nameEnd + 1);
                if (candidate.isEmpty()) {
                    continue;
                }
                String lower = candidate.toLowerCase(Locale.ROOT);
                if (CONTROL_KEYWORDS.contains(lower)) {
                    continue;
                }
                return i;
            }
            if (ch == ';' || ch == '{' || ch == '}') {
                break;
            }
        }
        return -1;
    }

    private int findMatchingParenForward(String text, int openPos) {
        int depth = 0;
        boolean inString = false;
        char stringChar = 0;
        for (int i = openPos; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (inString) {
                if (ch == stringChar && (i == openPos || text.charAt(i - 1) != '\\')) {
                    inString = false;
                }
                continue;
            }
            if (ch == '"' || ch == '\'') {
                inString = true;
                stringChar = ch;
                continue;
            }
            if (ch == '/' && i + 1 < text.length()) {
                char next = text.charAt(i + 1);
                if (next == '/') {
                    i = skipLineComment(text, i + 2);
                    continue;
                } else if (next == '*') {
                    i = skipBlockComment(text, i + 2);
                    continue;
                }
            }
            if (ch == '(') {
                depth++;
            } else if (ch == ')') {
                depth--;
                if (depth == 0) {
                    return i;
                }
            }
        }
        return -1;
    }

    private boolean isIdentifierPart(char ch) {
        return Character.isLetterOrDigit(ch) || ch == '_' || ch == '$';
    }

    private String simplifyOwnerName(String ownerToken) {
        if (ownerToken == null || ownerToken.trim().isEmpty()) {
            return "";
        }
        String trimmed = ownerToken.trim();
        int generic = trimmed.indexOf('<');
        if (generic >= 0) {
            trimmed = trimmed.substring(0, generic);
        }
        while (trimmed.endsWith("[]")) {
            trimmed = trimmed.substring(0, trimmed.length() - 2);
        }
        int lastDot = trimmed.lastIndexOf('.');
        if (lastDot >= 0 && lastDot < trimmed.length() - 1) {
            return trimmed.substring(lastDot + 1);
        }
        if (trimmed.endsWith("()")) {
            trimmed = trimmed.substring(0, trimmed.length() - 2);
        }
        return trimmed;
    }

    private int computeLineNumber(String text, int offset) {
        int line = 1;
        for (int i = 0; i < offset && i < text.length(); i++) {
            if (text.charAt(i) == '\n') {
                line++;
            }
        }
        return line;
    }

    private String buildMethodExplanation(MethodInfo info) {
        StringBuilder html = new StringBuilder();
        html.append("<html><body style='margin:0;font-family:")
                .append(htmlEscape(UI_FONT.getFamily()))
                .append(";font-size:")
                .append(UI_FONT.getSize())
                .append("px;color:#1f2328;'>");
        html.append("<h3 style='margin:0 0 6px 0;color:#1a3e85;'>")
                .append("Phương thức <code>")
                .append(htmlEscape(info.name))
                .append("</code></h3>");
        if (info.inherited && info.ownerType != null && !info.ownerType.isEmpty()) {
            html.append("<p style='margin:0 0 8px 0;color:#1d4ed8;'>Kế thừa từ lớp <code>")
                    .append(htmlEscape(info.ownerType))
                    .append("</code>.</p>");
        }
        html.append("<p style='margin:0 0 8px 0;color:#374151;'>")
                .append(htmlEscape(generateMethodSummary(info)))
                .append("</p>");
        html.append("<ul style='margin:0 0 8px 16px;padding:0;color:#374151;'>");
        html.append("<li><b>Loại thành phần:</b> ").append(htmlEscape(info.getElementKind())).append("</li>");
        if (info.ownerType != null && !info.ownerType.isEmpty()) {
            html.append("<li><b>Ngữ cảnh/Đối tượng:</b> ")
                    .append(htmlEscape(info.ownerType))
                    .append("</li>");
        }
        html.append("<li><b>Dòng định nghĩa:</b> ").append(info.lineNumber).append("</li>");
        String accessLabel = info.getElementKind().equals("Lời gọi API")
                ? "Phụ thuộc định nghĩa của thư viện"
                : info.getReadableAccess();
        html.append("<li><b>Mức truy cập:</b> ")
                .append(htmlEscape(accessLabel))
                .append("</li>");
        if (!info.modifiers.isEmpty()) {
            html.append("<li><b>Từ khóa bổ sung:</b> ")
                    .append(htmlEscape(info.modifiers))
                    .append("</li>");
        }
        if (info.constructor) {
            html.append("<li><b>Kiểu trả về:</b> Hàm khởi tạo, không trả về giá trị.</li>");
        } else if ("void".equals(info.returnType)) {
            html.append("<li><b>Kiểu trả về:</b> Không trả về giá trị (<code>void</code>).</li>");
        } else {
            html.append("<li><b>Kiểu trả về:</b> <code>")
                    .append(htmlEscape(info.returnType))
                    .append("</code>.</li>");
        }
        if (!info.params.isEmpty()) {
            html.append("<li><b>Tham số:</b><ul style='margin:4px 0 4px 16px;padding:0;'>");
            for (ParamInfo p : info.params) {
                html.append("<li><code>")
                        .append(htmlEscape(p.type))
                        .append(" ")
                        .append(htmlEscape(p.name))
                        .append("</code>");
                html.append(" - ").append(htmlEscape(describeParameter(p, info)));
                html.append("</li>");
            }
            html.append("</ul></li>");
        } else {
            html.append("<li><b>Tham số:</b> Không có.</li>");
        }
        if (!info.throwsClause.isEmpty()) {
            html.append("<li><b>Ngoại lệ có thể ném:</b> ")
                    .append(htmlEscape(info.throwsClause.replace("throws", "").trim()))
                    .append("</li>");
        }
        html.append("<li><b>Gợi ý học tập:</b> ")
                .append(htmlEscape(buildLearningTip(info)))
                .append("</li>");
        html.append("</ul>");
        html.append("<div style='margin-top:8px;padding:8px;background:#eef3ff;border-radius:6px;'>")
                .append("<b>Tài liệu thêm:</b> ")
                .append(htmlEscape(buildExtraReference(info)))
                .append("</div>");
        html.append("</body></html>");
        return html.toString();
    }

    private String generateMethodSummary(MethodInfo info) {
        String builtIn = builtInApiSummary(info);
        if (builtIn != null) {
            return builtIn;
        }

        if (info.constructor) {
            return "Khởi tạo một đối tượng \"" + info.ownerType + "\" với dữ liệu đầu vào tương ứng.";
        }
        String name = info.name;
        if (name.startsWith("get") && info.params.isEmpty()) {
            String property = decapitalize(name.substring(3));
            return "Trả về giá trị của thuộc tính \"" + property + "\".";
        }
        if (name.startsWith("set") && info.params.size() == 1) {
            String property = decapitalize(name.substring(3));
            return "Gán giá trị mới cho thuộc tính \"" + property + "\" dựa trên tham số truyền vào.";
        }
        if (name.startsWith("is") && info.params.isEmpty()) {
            String property = decapitalize(name.substring(2));
            return "Trả về trạng thái kiểm tra \"" + property + "\" ở dạng boolean.";
        }
        if (name.startsWith("has") && info.params.isEmpty()) {
            String property = decapitalize(name.substring(3));
            return "Kiểm tra xem \"" + property + "\" có tồn tại hoặc thỏa điều kiện hay không.";
        }
        if (name.startsWith("add") && info.params.size() == 1) {
            String target = decapitalize(name.substring(3));
            return "Thêm một phần tử \"" + target + "\" mới vào cấu trúc dữ liệu hoặc tập hợp của lớp.";
        }
        if (name.startsWith("remove") && info.params.size() >= 1) {
            String target = decapitalize(name.substring(6));
            return "Loại bỏ \"" + target + "\" khỏi cấu trúc dữ liệu tương ứng.";
        }
        if (name.startsWith("calculate") || name.startsWith("compute")) {
            return "Thực hiện phép tính hoặc xử lý nghiệp vụ để trả về kết quả phù hợp.";
        }
        if (name.startsWith("update")) {
            String target = decapitalize(name.substring(6));
            return "Cập nhật trạng thái hoặc dữ liệu cho \"" + target + "\".";
        }
        if (name.startsWith("load")) {
            return "Tải dữ liệu từ nguồn ngoài vào bộ nhớ của đối tượng.";
        }
        if (name.startsWith("save")) {
            return "Lưu dữ liệu hiện tại của đối tượng ra nguồn lưu trữ bên ngoài.";
        }
        if (name.startsWith("validate")) {
            return "Kiểm tra tính hợp lệ của dữ liệu trước khi xử lý tiếp.";
        }
        if (name.startsWith("process")) {
            return "Thực hiện chuỗi bước xử lý chính trên dữ liệu đầu vào.";
        }
        if ("toString".equals(name)) {
            return "Chuyển đổi đối tượng thành chuỗi mô tả nội dung.";
        }
        if ("hashCode".equals(name)) {
            return "Sinh mã băm đại diện cho đối tượng nhằm hỗ trợ lưu trữ trong cấu trúc dữ liệu băm.";
        }
        if ("equals".equals(name) && info.params.size() == 1) {
            return "So sánh đối tượng hiện tại với đối tượng khác để xác định sự tương đương.";
        }
        if ("compareTo".equals(name) && info.params.size() == 1) {
            return "So sánh thứ tự giữa hai đối tượng để phục vụ sắp xếp.";
        }
        if ("clone".equals(name) && info.params.isEmpty()) {
            return "Tạo một bản sao (shallow copy) của đối tượng hiện tại.";
        }
        if ("finalize".equals(name) && info.params.isEmpty()) {
            return "Dọn dẹp tài nguyên trước khi đối tượng bị thu gom rác (không nên lạm dụng).";
        }
        return GENERIC_SUMMARY;
    }

    private String describeParameter(ParamInfo param, MethodInfo method) {
        String lowerName = method.name.toLowerCase(Locale.ROOT);
        String ownerKey = canonicalOwnerKey(method.ownerType);
        int index = method.params.indexOf(param);

        if (lowerName.startsWith("set") && method.params.size() == 1) {
            return "Giá trị mới cần gán cho thuộc tính.";
        }
        if (lowerName.startsWith("add") && method.params.size() == 1) {
            return "Phần tử cần được thêm vào.";
        }
        if (lowerName.startsWith("update")) {
            return "Nguồn dữ liệu hoặc giá trị dùng để cập nhật.";
        }
        if (lowerName.startsWith("validate")) {
            return "Đối tượng hoặc dữ liệu cần kiểm tra tính hợp lệ.";
        }
        if (lowerName.startsWith("remove")) {
            return "Định danh hoặc đối tượng cần xóa bỏ.";
        }
        if (lowerName.startsWith("load")) {
            return "Thông tin về nguồn dữ liệu cần tải.";
        }
        if (lowerName.startsWith("save")) {
            return "Đích lưu trữ hoặc dữ liệu cần được lưu lại.";
        }
        if (lowerName.startsWith("find") || lowerName.startsWith("search")) {
            return "Điều kiện hoặc khóa để truy vấn dữ liệu.";
        }
        if (lowerName.startsWith("sort")) {
            return index == 0 ? "Tập dữ liệu cần sắp xếp." : "Comparator quyết định thứ tự (có thể null).";
        }

        if ("stringbuilder".equals(ownerKey)) {
            if ("append".equals(lowerName)) {
                return "Giá trị sẽ được ghép vào cuối chuỗi hiện tại (có thể là số, chuỗi, đối tượng).";
            }
            if ("insert".equals(lowerName)) {
                if (index == 0) {
                    return "Chỉ số chèn (0-based).";
                }
                if (index == 1) {
                    return "Giá trị sẽ được chèn vào đệm.";
                }
            }
            if ("delete".equals(lowerName)) {
                if (index == 0) {
                    return "Chỉ số bắt đầu đoạn cần xóa.";
                }
                if (index == 1) {
                    return "Chỉ số kết thúc (exclusive) của đoạn cần xóa.";
                }
            }
            if ("replace".equals(lowerName)) {
                if (index == 0) {
                    return "Chỉ số bắt đầu đoạn cần thay.";
                }
                if (index == 1) {
                    return "Chỉ số kết thúc (exclusive) của đoạn cần thay.";
                }
                if (index == 2) {
                    return "Chuỗi mới thay thế đoạn cũ.";
                }
            }
        }

        if ("string".equals(ownerKey)) {
            if ("substring".equals(lowerName)) {
                if (index == 0) {
                    return "Chỉ số bắt đầu (inclusive).";
                }
                if (index == 1) {
                    return "Chỉ số kết thúc (exclusive).";
                }
            }
            if ("split".equals(lowerName)) {
                if (index == 0) {
                    return "Biểu thức chính quy dùng để tách chuỗi.";
                }
                if (index == 1) {
                    return "Giới hạn số phần tử trả về (<=0 nghĩa là không giới hạn).";
                }
            }
            if ("replace".equals(lowerName) || "replacefirst".equals(lowerName)) {
                if (index == 0) {
                    return "Chuỗi hoặc mẫu cần thay thế.";
                }
                if (index == 1) {
                    return "Chuỗi thay thế.";
                }
            }
            if ("replaceall".equals(lowerName)) {
                if (index == 0) {
                    return "Biểu thức chính quy mô tả nội dung cần thay.";
                }
                if (index == 1) {
                    return "Chuỗi thay thế cho mỗi lần khớp.";
                }
            }
            if ("indexof".equals(lowerName) || "lastindexof".equals(lowerName)) {
                return "Chuỗi hoặc ký tự cần tìm.";
            }
            if ("format".equals(lowerName)) {
                if (index == 0) {
                    return "Mẫu định dạng (pattern).";
                }
                return "Đối số sẽ được chèn vào mẫu định dạng.";
            }
        }

        if ("list".equals(ownerKey)) {
            if ("add".equals(lowerName) && method.params.size() == 2) {
                if (index == 0) {
                    return "Chỉ số cần chèn phần tử.";
                }
                if (index == 1) {
                    return "Phần tử sẽ được chèn.";
                }
            }
            if ("set".equals(lowerName)) {
                if (index == 0) {
                    return "Chỉ số phần tử cần ghi đè.";
                }
                if (index == 1) {
                    return "Giá trị mới thay thế phần tử cũ.";
                }
            }
            if ("sublist".equals(lowerName)) {
                if (index == 0) {
                    return "Chỉ số bắt đầu (inclusive) của view con.";
                }
                if (index == 1) {
                    return "Chỉ số kết thúc (exclusive) của view con.";
                }
            }
        }

        if ("map".equals(ownerKey)) {
            if ("put".equals(lowerName) || "putifabsent".equals(lowerName)) {
                if (index == 0) {
                    return "Khóa cần thêm/cập nhật.";
                }
                if (index == 1) {
                    return "Giá trị gắn với khóa.";
                }
            }
            if ("get".equals(lowerName) || "getordefault".equals(lowerName)) {
                if (index == 0) {
                    return "Khóa cần truy vấn.";
                }
                if (index == 1) {
                    return "Giá trị mặc định khi khóa không tồn tại.";
                }
            }
            if ("merge".equals(lowerName)) {
                if (index == 0) {
                    return "Khóa cần hợp nhất.";
                }
                if (index == 1) {
                    return "Giá trị mới muốn ghép vào.";
                }
                if (index == 2) {
                    return "Hàm gộp hai giá trị cũ và mới.";
                }
            }
            if ("computeifabsent".equals(lowerName)) {
                if (index == 0) {
                    return "Khóa cần kiểm tra.";
                }
                if (index == 1) {
                    return "Hàm tạo giá trị khi khóa chưa tồn tại.";
                }
            }
            if ("computeifpresent".equals(lowerName)) {
                if (index == 0) {
                    return "Khóa cần cập nhật.";
                }
                if (index == 1) {
                    return "Hàm xử lý (khóa, giá trị cũ) để tạo giá trị mới.";
                }
            }
        }

        if ("map.entry".equals(ownerKey) && "setvalue".equals(lowerName)) {
            return "Giá trị mới cho entry hiện tại.";
        }

        if ("optional".equals(ownerKey)) {
            if ("of".equals(lowerName) || "ofnullable".equals(lowerName)) {
                return "Giá trị nguồn sẽ được bọc trong Optional.";
            }
            if ("orelse".equals(lowerName)) {
                return "Giá trị mặc định trả về khi Optional rỗng.";
            }
            if ("orelseget".equals(lowerName)) {
                return "Supplier sinh giá trị mặc định nếu Optional rỗng.";
            }
            if ("orelsethrow".equals(lowerName)) {
                return "Supplier tạo ngoại lệ sẽ bị ném khi Optional rỗng.";
            }
            if ("map".equals(lowerName) || "flatmap".equals(lowerName) || "filter".equals(lowerName)) {
                return "Hàm thao tác với giá trị bên trong Optional.";
            }
        }

        if ("stream".equals(ownerKey)) {
            if ("map".equals(lowerName)) {
                return "Function chuyển đổi từng phần tử.";
            }
            if ("filter".equals(lowerName)) {
                return "Predicate quyết định giữ lại phần tử.";
            }
            if ("flatmap".equals(lowerName)) {
                return "Function biến phần tử thành Stream con.";
            }
            if ("collect".equals(lowerName)) {
                return "Collector xác định cách thu thập cuối cùng.";
            }
            if ("reduce".equals(lowerName)) {
                if (method.params.size() == 2) {
                    return index == 0 ? "Giá trị khởi tạo để gộp." : "Hàm gộp hai phần tử thành một.";
                }
                if (method.params.size() == 3) {
                    if (index == 0) {
                        return "Giá trị khởi tạo.";
                    }
                    if (index == 1) {
                        return "Accumulator gộp phần tử vào kết quả.";
                    }
                    if (index == 2) {
                        return "Hàm kết hợp kết quả song song.";
                    }
                }
            }
        }

        if ("collections".equals(ownerKey) && "sort".equals(lowerName)) {
            if (index == 0) {
                return "Danh sách cần sắp xếp.";
            }
            if (index == 1) {
                return "Comparator quyết định thứ tự (có thể null).";
            }
        }

        if ("arrays".equals(ownerKey)) {
            if ("sort".equals(lowerName)) {
                if (index == 0) {
                    return "Mảng cần sắp xếp.";
                }
                if (index == 1) {
                    return "Chỉ số bắt đầu hoặc Comparator (tùy overload).";
                }
            }
            if ("copyof".equals(lowerName) || "copyofrange".equals(lowerName)) {
                return index == 0 ? "Mảng nguồn." : "Kích thước hoặc giới hạn đoạn sao chép.";
            }
            if ("fill".equals(lowerName)) {
                if (index == 0) {
                    return "Mảng cần gán.";
                }
                if (index == 1) {
                    return "Giá trị dùng để gán cho mọi phần tử.";
                }
            }
        }

        if ("math".equals(ownerKey)) {
            if ("pow".equals(lowerName)) {
                return index == 0 ? "Cơ số (base)." : "Số mũ (exponent).";
            }
            if ("max".equals(lowerName) || "min".equals(lowerName)) {
                return index == 0 ? "Đối số thứ nhất." : "Đối số thứ hai.";
            }
        }

        if ("system".equals(ownerKey) && "arraycopy".equals(lowerName)) {
            switch (index) {
                case 0:
                    return "Mảng nguồn cần sao chép.";
                case 1:
                    return "Vị trí bắt đầu trong mảng nguồn.";
                case 2:
                    return "Mảng đích nhận dữ liệu.";
                case 3:
                    return "Vị trí bắt đầu trong mảng đích.";
                case 4:
                    return "Số phần tử cần sao chép.";
            }
        }

        if ("printstream".equals(ownerKey) && "printf".equals(lowerName)) {
            if (index == 0) {
                return "Chuỗi định dạng.";
            }
            return "Đối số được chèn vào mẫu định dạng.";
        }

        if ("array".equals(ownerKey) && "fill".equals(lowerName)) {
            return index == 0 ? "Mảng cần gán." : "Giá trị dùng để gán.";
        }

        if (("list".equals(ownerKey) || "set".equals(ownerKey) || "map".equals(ownerKey)) && "of".equals(lowerName)) {
            return "Phần tử dùng để tạo collection bất biến.";
        }

        return "Tham số kiểu " + param.type + " phục vụ cho nghiệp vụ của phương thức.";
    }

    private String buildLearningTip(MethodInfo info) {
        String lowerName = info.name.toLowerCase(Locale.ROOT);
        String ownerKey = canonicalOwnerKey(info.ownerType);
        if (info.constructor) {
            return "Ôn tập cách viết hàm khởi tạo: gán giá trị mặc định, sử dụng từ khóa this()/super() và quy tắc trật tự lệnh.";
        }
        if ("stringbuilder".equals(ownerKey)) {
            return "Ôn lại cách dùng StringBuilder để nối chuỗi hiệu quả, tránh tạo nhiều đối tượng String trung gian.";
        }
        if ("string".equals(ownerKey)) {
            return "Nắm vững các thao tác xử lý chuỗi, chú ý sự bất biến (immutable) của String trong Java.";
        }
        if ("stream".equals(ownerKey)) {
            return "Luyện tập pipeline Stream (map/filter/reduce) và cách viết lambda biểu đạt rõ ràng.";
        }
        if ("collections".equals(ownerKey) || "list".equals(ownerKey) || "set".equals(ownerKey) || "queue".equals(ownerKey)) {
            return "Ôn Collections Framework: đặc điểm từng cấu trúc, độ phức tạp và khi nào dùng ArrayList, LinkedList, HashSet.";
        }
        if ("arrays".equals(ownerKey) || "array".equals(ownerKey)) {
            return "Nhớ rằng mảng có kích thước cố định; luyện tập sử dụng Arrays để xử lý nhanh chóng.";
        }
        if ("optional".equals(ownerKey)) {
            return "Rèn luyện cách tránh null bằng Optional, kết hợp map/flatMap/orElse.";
        }
        if ("map".equals(ownerKey) || "map.entry".equals(ownerKey)) {
            return "Ôn lại thao tác với cấu trúc key/value (Map), quản lý khóa trùng và hàm merge/compute.";
        }
        if ("math".equals(ownerKey)) {
            return "Ôn tập hàm toán học cơ bản của Math và sự khác biệt giữa int/double.";
        }
        if ("printstream".equals(ownerKey)) {
            return "Luyện tập định dạng đầu ra với print/printf và quản lý luồng IO (flush/close).";
        }
        if (lowerName.startsWith("get") || lowerName.startsWith("is") || lowerName.startsWith("has")) {
            return "Nhớ rằng getter không nên thay đổi trạng thái; luyện tập quy tắc JavaBean (get/is) và nguyên tắc đóng gói.";
        }
        if (lowerName.startsWith("set") || lowerName.startsWith("update")) {
            return "Xem lại cách kiểm tra đầu vào trước khi gán thuộc tính, kết hợp ràng buộc business và fluent API nếu cần.";
        }
        if (lowerName.startsWith("add") || lowerName.startsWith("remove")) {
            return "Ôn lại cách quản lý danh sách/tập hợp: kiểm tra null, tránh trùng dữ liệu và cân nhắc Collections.unmodifiableList khi trả về.";
        }
        if (lowerName.startsWith("validate")) {
            return "Tập trung vào các kỹ thuật kiểm tra dữ liệu (Bean Validation, thủ công) và cách phản hồi lỗi hợp lý.";
        }
        if (lowerName.startsWith("load") || lowerName.startsWith("save")) {
            return "Xem lại I/O stream, xử lý ngoại lệ và đóng tài nguyên đúng cách (try-with-resources).";
        }
        if (lowerName.startsWith("process") || lowerName.startsWith("calculate") || lowerName.startsWith("compute")) {
            return "Nhớ chia nhỏ logic thành các bước rõ ràng, áp dụng SOLID để dễ kiểm thử và tái sử dụng.";
        }
        if (lowerName.equals("map") || lowerName.equals("filter") || lowerName.equals("collect") || lowerName.equals("reduce")) {
            return "Ôn lại thao tác Stream: hiểu khi nào dùng intermediate vs terminal operation.";
        }
        if (lowerName.startsWith("size") || lowerName.equals("clear") || lowerName.equals("contains")) {
            return "Tổng ôn về Collections Framework: cách kiểm tra kích thước, trạng thái rỗng và tìm kiếm phần tử.";
        }
        if (lowerName.equals("add") || lowerName.equals("remove") || lowerName.equals("addall")) {
            return "Ôn tập các thao tác thêm/xóa trong List, Set, Queue và tác động đến iterator.";
        }
        if ("equals".equals(lowerName) || "hashcode".equals(lowerName) || "compareto".equals(lowerName)) {
            return "Ôn quy tắc hợp đồng equals/hashCode/compareTo và đảm bảo tính nhất quán khi ghi đè.";
        }
        if (info.hasModifier("static")) {
            return "Phân biệt phương thức static và instance; cân nhắc dùng static cho tiện ích thuần (utility) hoặc factory.";
        }
        if (info.hasModifier("abstract")) {
            return "Tìm hiểu cơ chế kế thừa/ghi đè và cách lớp con hiện thực phương thức trừu tượng.";
        }
        if (info.hasModifier("synchronized")) {
            return "Ôn kiến thức đồng bộ hóa, tránh deadlock và cân nhắc dùng java.util.concurrent hiện đại.";
        }
        if (!info.throwsClause.isEmpty()) {
            return "Rèn luyện xử lý ngoại lệ: phân biệt checked/unchecked, nên ném lại hay gói thành RuntimeException.";
        }
        return "Ôn cấu trúc phương thức trong Java: chữ ký, phạm vi truy cập, quy ước đặt tên và viết tài liệu rõ ràng.";
    }

    private String buildExtraReference(MethodInfo info) {
        String lowerName = info.name.toLowerCase(Locale.ROOT);
        String ownerKey = canonicalOwnerKey(info.ownerType);
        if (info.constructor) {
            return "Chủ đề: Constructor, từ khóa this/super, chuỗi khởi tạo (constructor chaining).";
        }
        if (lowerName.startsWith("get") || lowerName.startsWith("set") || lowerName.startsWith("is") || lowerName.startsWith("has")) {
            return "Chủ đề: JavaBeans getter/setter, nguyên tắc đóng gói (encapsulation).";
        }
        if (lowerName.startsWith("add") || lowerName.startsWith("remove")) {
            return "Chủ đề: Collections Framework (List, Set, Map) và quy tắc đồng bộ hóa dữ liệu.";
        }
        if (lowerName.startsWith("validate")) {
            return "Chủ đề: Bean Validation (JSR 380), kỹ thuật kiểm tra đầu vào, ngoại lệ IllegalArgumentException.";
        }
        if (lowerName.startsWith("load") || lowerName.startsWith("save")) {
            return "Chủ đề: Java I/O, serialization, try-with-resources, xử lý ngoại lệ checked.";
        }
        if (lowerName.startsWith("process") || lowerName.startsWith("calculate") || lowerName.startsWith("compute")) {
            return "Chủ đề: Tách lớp dịch vụ (service layer), Clean Code cho hàm tính toán, viết unit test.";
        }
        if (lowerName.equals("map") || lowerName.equals("filter") || lowerName.equals("collect") || lowerName.equals("reduce")) {
            return "Chủ đề: Java Stream API, lập trình hàm, Collector tuỳ biến.";
        }
        if (lowerName.startsWith("size") || lowerName.equals("clear") || lowerName.equals("contains")) {
            return "Chủ đề: Collection API (List/Set/Queue) và độ phức tạp thao tác.";
        }
        if (lowerName.equals("add") || lowerName.equals("remove") || lowerName.equals("addall")) {
            return "Chủ đề: Iterator, ConcurrentModificationException, thao tác trên danh sách.";
        }
        if ("equals".equals(lowerName) || "hashcode".equals(lowerName) || "compareto".equals(lowerName)) {
            return "Chủ đề: Hợp đồng equals/hashCode, interface Comparable/Comparator.";
        }
        if ("toString".equals(lowerName)) {
            return "Chủ đề: Ghi đè toString, StringBuilder, debug-friendly output.";
        }
        if ("stringbuilder".equals(ownerKey)) {
            return "Chủ đề: StringBuilder/StringBuffer, hiệu năng nối chuỗi.";
        }
        if ("string".equals(ownerKey)) {
            return "Chủ đề: API xử lý chuỗi, immutable string, encoding.";
        }
        if (ownerKey.contains("stream")) {
            return "Chủ đề: Stream pipeline, lazy evaluation, Collector chuẩn.";
        }
        if ("collections".equals(ownerKey) || ownerKey.contains("list") || ownerKey.contains("set")) {
            return "Chủ đề: Collections utility class, danh sách, tập hợp và các thuật toán sắp xếp/tìm kiếm.";
        }
        if ("arrays".equals(ownerKey)) {
            return "Chủ đề: Lớp trợ giúp Arrays, thao tác trên mảng và hiệu năng.";
        }
        if ("optional".equals(ownerKey)) {
            return "Chủ đề: Optional API, lập trình tránh NullPointerException.";
        }
        if ("math".equals(ownerKey)) {
            return "Chủ đề: Thư viện toán học cơ bản Math, xử lý số chính xác.";
        }
        if (info.hasModifier("static")) {
            return "Chủ đề: Từ khóa static, factory method, tiện ích (utility class).";
        }
        if (info.hasModifier("abstract")) {
            return "Chủ đề: Lớp trừu tượng, interface, lập trình hướng đối tượng nâng cao.";
        }
        if (!info.throwsClause.isEmpty()) {
            return "Chủ đề: Xử lý ngoại lệ, throws vs throw, thiết kế ngoại lệ tuỳ biến.";
        }
        if (info.hasModifier("synchronized")) {
            return "Chủ đề: Đồng bộ hóa, monitor lock, java.util.concurrent.";
        }
        return "Chủ đề: Thiết kế phương thức hiệu quả, tài liệu JavaDoc, viết test cho logic nghiệp vụ.";
    }

    private String builtInApiSummary(MethodInfo info) {
        String ownerKey = canonicalOwnerKey(info.ownerType);
        String name = info.name.toLowerCase(Locale.ROOT);
        int paramCount = info.params.size();

        String doc = lookupBuiltInDoc(ownerKey, name);
        if (doc == null) {
            boolean ownerKnown = OWNER_ALIASES.containsKey(ownerKey);
            if ((!ownerKnown || ownerKey.isEmpty()) && CHAR_SEQUENCE_METHODS.contains(name)) {
                doc = lookupBuiltInDoc("charsequence", name);
                if (doc == null) {
                    doc = lookupBuiltInDoc("string", name);
                }
            }
            if (doc == null && "length".equals(name)) {
                doc = lookupBuiltInDoc("array", name);
            }
        }
        if (doc != null) {
            return doc;
        }

        boolean isList = "list".equals(ownerKey);
        boolean isSet = "set".equals(ownerKey);
        boolean isQueue = "queue".equals(ownerKey);
        boolean isMap = "map".equals(ownerKey);
        boolean isStream = "stream".equals(ownerKey);
        boolean isCollections = "collections".equals(ownerKey);
        boolean isArraysUtil = "arrays".equals(ownerKey);
        boolean isStringBuilder = "stringbuilder".equals(ownerKey);
        boolean isSystem = "system".equals(ownerKey);
        boolean isPrintStream = "printstream".equals(ownerKey) || "out".equals(ownerKey) || "err".equals(ownerKey);
        boolean isMath = "math".equals(ownerKey);
        boolean isOptional = "optional".equals(ownerKey);
        boolean isArrayOwner = "array".equals(ownerKey);

        if (isList || isSet || isQueue) {
            switch (name) {
                case "add":
                    return "Thêm phần tử mới vào bộ sưu tập.";
                case "addall":
                    return "Thêm toàn bộ phần tử từ một bộ sưu tập khác.";
                case "remove":
                    return "Xóa phần tử theo giá trị hoặc chỉ số.";
                case "removeif":
                    return "Xóa mọi phần tử thỏa điều kiện của Predicate.";
                case "contains":
                    return "Kiểm tra xem bộ sưu tập có chứa phần tử cho trước không.";
                case "clear":
                    return "Xóa toàn bộ phần tử, đưa bộ sưu tập về trạng thái rỗng.";
                case "isempty":
                    return "Kiểm tra bộ sưu tập có đang rỗng hay không.";
                case "size":
                    return "Trả về số lượng phần tử hiện có.";
                case "get":
                    if (isList && paramCount == 1) {
                        return "Lấy phần tử tại vị trí (index) cụ thể trong danh sách.";
                    }
                    break;
                case "set":
                    if (isList && paramCount == 2) {
                        return "Ghi đè giá trị tại vị trí chỉ định trong danh sách.";
                    }
                    break;
                case "indexof":
                    if (isList) {
                        return "Tìm chỉ số đầu tiên của phần tử trong danh sách (hoặc -1 nếu không có).";
                    }
                    break;
                case "lastindexof":
                    if (isList) {
                        return "Tìm chỉ số cuối cùng của phần tử trong danh sách.";
                    }
                    break;
                case "peek":
                    if (isQueue) {
                        return "Xem phần tử đầu hàng mà không loại bỏ.";
                    }
                    break;
                case "poll":
                    if (isQueue) {
                        return "Lấy và loại bỏ phần tử đầu hàng; trả về null nếu rỗng.";
                    }
                    break;
                case "push":
                    if ("stack".equals(ownerKey)) {
                        return "Đẩy phần tử lên đỉnh ngăn xếp.";
                    }
                    break;
                case "pop":
                    if ("stack".equals(ownerKey)) {
                        return "Lấy và loại bỏ phần tử đỉnh ngăn xếp.";
                    }
                    break;
                case "sort":
                    if (isList) {
                        return "Sắp xếp danh sách theo thứ tự tự nhiên hoặc Comparator cung cấp.";
                    }
                    break;
            }
        }

        if (isMap) {
            switch (name) {
                case "put":
                    return "Thêm hoặc cập nhật cặp khóa-giá trị trong bảng ánh xạ.";
                case "putifabsent":
                    return "Chỉ thêm khóa-giá trị khi khóa chưa tồn tại.";
                case "get":
                    if (paramCount == 1) {
                        return "Lấy giá trị tương ứng với khóa cho trước (hoặc null nếu không tồn tại).";
                    }
                    break;
                case "containskey":
                    return "Kiểm tra xem khóa có tồn tại trong bảng ánh xạ không.";
                case "containsvalue":
                    return "Kiểm tra xem giá trị có tồn tại trong bảng ánh xạ không.";
                case "remove":
                    return "Xóa khóa (và giá trị) khỏi bảng ánh xạ.";
                case "keyset":
                    return "Trả về tập hợp tất cả khóa trong bảng ánh xạ.";
                case "values":
                    return "Trả về collection chứa toàn bộ giá trị.";
                case "entryset":
                    return "Trả về tập hợp các cặp khóa-giá trị (Entry).";
                case "size":
                    return "Trả về số lượng cặp khóa-giá trị hiện có.";
                case "clear":
                    return "Xóa sạch mọi cặp khóa-giá trị.";
                case "computeifabsent":
                    return "Tạo giá trị mới khi khóa chưa tồn tại, sử dụng hàm cung cấp.";
                case "merge":
                    return "Kết hợp giá trị mới với giá trị cũ bằng hàm hợp nhất.";
            }
        }

        if (isCollections) {
            switch (name) {
                case "sort":
                    return "Sắp xếp danh sách theo thứ tự tự nhiên hoặc Comparator.";
                case "reverse":
                    return "Đảo ngược thứ tự các phần tử của danh sách.";
                case "shuffle":
                    return "Xáo trộn danh sách ngẫu nhiên.";
                case "binarysearch":
                    return "Tìm kiếm nhị phân trong danh sách đã sắp xếp.";
                case "unmodifiablelist":
                    return "Trả về danh sách chỉ đọc, ngăn thay đổi dữ liệu gốc.";
                case "synchronizedlist":
                    return "Bọc danh sách để an toàn trong môi trường đa luồng.";
            }
        }

        if (isStream) {
            switch (name) {
                case "map":
                    return "Biến đổi từng phần tử của Stream thành giá trị mới.";
                case "filter":
                    return "Lọc giữ những phần tử thỏa điều kiện Predicate.";
                case "collect":
                    return "Thu thập Stream thành kết quả cuối cùng bằng Collector.";
                case "reduce":
                    return "Gộp các phần tử Stream thành một giá trị duy nhất thông qua accumulator.";
                case "sorted":
                    return "Sắp xếp lại thứ tự phần tử trong Stream.";
            }
        }

        if (isArraysUtil) {
            switch (name) {
                case "sort":
                    return "Sắp xếp mảng theo thứ tự tự nhiên hoặc Comparator.";
                case "binarysearch":
                    return "Tìm kiếm nhị phân phần tử trong mảng đã sắp xếp.";
                case "fill":
                    return "Gán tất cả phần tử mảng thành giá trị cho trước.";
                case "copyof":
                    return "Tạo mảng mới sao chép các phần tử từ mảng gốc.";
                case "equals":
                    return "So sánh hai mảng theo từng phần tử.";
            }
        }

        if (isMath) {
            switch (name) {
                case "abs":
                    return "Trả về giá trị tuyệt đối của số truyền vào.";
                case "max":
                    return "Trả về số lớn hơn giữa hai đối số.";
                case "min":
                    return "Trả về số nhỏ hơn giữa hai đối số.";
                case "pow":
                    return "Tính lũy thừa theo cơ số và số mũ.";
                case "sqrt":
                    return "Tính căn bậc hai của số truyền vào.";
                case "round":
                    return "Làm tròn số về giá trị gần nhất.";
                case "random":
                    return "Sinh số double ngẫu nhiên trong khoảng [0,1).";
            }
        }

        if (isOptional) {
            switch (name) {
                case "of":
                    return "Tạo Optional chứa giá trị không null.";
                case "empty":
                    return "Tạo Optional rỗng.";
                case "ofnullable":
                    return "Tạo Optional từ giá trị có thể null.";
                case "ispresent":
                    return "Kiểm tra Optional có chứa giá trị không.";
                case "ifpresent":
                    return "Thực thi hành động nếu Optional chứa giá trị.";
                case "orelse":
                    return "Trả về giá trị bên trong hoặc giá trị mặc định.";
            }
        }

        if (isSystem) {
            switch (name) {
                case "arraycopy":
                    return "Sao chép một đoạn mảng sang mảng đích cực nhanh (native).";
                case "currenttimemillis":
                    return "Trả về thời gian hiện tại tính bằng mili giây kể từ epoch.";
                case "nanotime":
                    return "Trả về thời gian độ phân giải cao (nano giây) phục vụ đo hiệu năng.";
            }
        }

        if (isPrintStream) {
            switch (name) {
                case "println":
                    return "In chuỗi/giá trị ra luồng chuẩn kèm ký tự xuống dòng.";
                case "print":
                    return "In chuỗi/giá trị ra luồng chuẩn không xuống dòng.";
                case "printf":
                    return "In chuỗi theo định dạng (format) ra luồng chuẩn.";
                case "flush":
                    return "Đẩy toàn bộ dữ liệu đang đệm xuống thiết bị xuất.";
            }
        }

        if (isArrayOwner && "length".equals(name)) {
            return "Thuộc tính cho biết số phần tử hiện có trong mảng.";
        }

        // Fallback chung cho một số tên phổ biến (không phụ thuộc owner)
        switch (name) {
            case "println":
                return "In chuỗi/giá trị ra luồng chuẩn kèm ký tự xuống dòng.";
            case "print":
                return "In chuỗi/giá trị ra luồng chuẩn không xuống dòng.";
            case "printf":
                return "In chuỗi theo định dạng (format) ra luồng chuẩn.";
            case "valueof":
                return "Chuyển đổi giá trị bất kỳ sang chuỗi đại diện.";
            case "parseint":
                return "Phân tích chuỗi thành số nguyên (Integer.parseInt).";
            case "parsedouble":
                return "Phân tích chuỗi thành số thực dấu chấm động.";
            case "parseboolean":
                return "Phân tích chuỗi thành giá trị boolean.";
            case "size":
                return "Trả về số phần tử hiện có.";
            case "clear":
                return "Xóa tất cả phần tử, đưa cấu trúc về trạng thái rỗng.";
            case "contains":
                if (paramCount == 1) {
                    return "Kiểm tra xem có tồn tại phần tử hoặc khóa tương ứng hay không.";
                }
                break;
            case "add":
                return "Thêm phần tử mới vào cấu trúc dữ liệu hoặc danh sách.";
            case "remove":
                return "Xóa phần tử theo giá trị hoặc điều kiện.";
            case "put":
                return "Thêm hoặc cập nhật cặp khóa-giá trị trong bảng ánh xạ.";
            case "get":
                return "Lấy phần tử theo chỉ số hoặc khóa tương ứng.";
            case "set":
                return "Ghi đè giá trị tại vị trí hoặc khóa nhất định.";
            case "offer":
                return "Thêm phần tử vào hàng đợi theo thứ tự phù hợp.";
            case "peek":
                return "Xem phần tử đầu hàng (hoặc đỉnh ngăn xếp) mà không loại bỏ.";
            case "poll":
                return "Lấy và loại bỏ phần tử đầu hàng (hoặc đầu deque).";
            case "push":
                return "Đưa phần tử lên đỉnh ngăn xếp.";
            case "pop":
                return "Lấy và loại bỏ phần tử đỉnh ngăn xếp.";
            case "map":
                return "Biến đổi từng phần tử Stream thành giá trị mới.";
            case "filter":
                return "Giữ lại những phần tử Stream thỏa điều kiện Predicate.";
            case "collect":
                return "Thu thập phần tử Stream về cấu trúc kết quả (Collectors).";
            case "reduce":
                return "Gộp các phần tử Stream thành một giá trị duy nhất thông qua accumulator.";
            case "iterator":
                return "Trả về Iterator để duyệt tuần tự qua từng phần tử.";
            case "stream":
                return "Trả về Stream để xử lý dữ liệu theo kiểu hàm (Java 8+).";
            case "parallelstream":
                return "Tạo Stream chạy song song, tận dụng nhiều lõi CPU.";
            case "clone":
                if (paramCount == 0) {
                    return "Tạo bản sao nông (shallow copy) của đối tượng hiện tại.";
                }
                break;
            case "notify":
            case "notifyall":
                return "Đánh thức luồng đang chờ trên khóa của đối tượng (lập trình đa luồng).";
            case "wait":
                return "Đặt luồng hiện tại vào trạng thái chờ cho đến khi được đánh thức.";
            case "sleep":
                return "Đưa luồng vào trạng thái ngủ trong thời gian xác định.";
        }

        return null;
    }

    private String detectPrimaryTypeName(String content, String fallbackName) {
        Pattern typePattern = Pattern.compile("\\b(class|interface|enum)\\s+(\\w+)");
        Matcher m = typePattern.matcher(content);
        if (m.find()) {
            return m.group(2);
        }
        if (fallbackName != null) {
            return fallbackName.replaceFirst("\\.java$", "");
        }
        return "";
    }

    private String normalizeSpace(String input) {
        if (input == null) {
            return "";
        }
        return input.trim().replaceAll("\\s+", " ");
    }

    private String convertTypeForDisplay(String rawType) {
        if (rawType == null) {
            return "";
        }
        String trimmed = rawType.trim();
        if (trimmed.isEmpty() || "var".equals(trimmed)) {
            return "";
        }
        return simplifyOwnerName(trimmed);
    }

    private String lookupBuiltInDoc(String ownerKey, String methodKey) {
        if (ownerKey == null || methodKey == null) {
            return null;
        }
        Map<String, String> docs = BUILTIN_DOCS.get(ownerKey.toLowerCase(Locale.ROOT));
        if (docs == null) {
            return null;
        }
        return docs.get(methodKey);
    }

    private String canonicalOwnerKey(String owner) {
        if (owner == null) {
            return "";
        }
        String key = owner.trim().toLowerCase(Locale.ROOT);
        if (key.isEmpty()) {
            return "";
        }
        int generic = key.indexOf('<');
        if (generic >= 0) {
            key = key.substring(0, generic);
        }
        while (key.endsWith("[]")) {
            key = key.substring(0, key.length() - 2);
        }
        if (key.endsWith("()")) {
            key = key.substring(0, key.length() - 2);
        }
        return OWNER_ALIASES.getOrDefault(key, key);
    }

    private String groupSafely(Matcher matcher, int index) {
        if (index <= matcher.groupCount()) {
            String value = matcher.group(index);
            return value != null ? value : "";
        }
        return "";
    }

    private String htmlEscape(String input) {
        if (input == null) {
            return "";
        }
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private String decapitalize(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        if (text.length() == 1) {
            return text.toLowerCase(Locale.ROOT);
        }
        return text.substring(0, 1).toLowerCase(Locale.ROOT) + text.substring(1);
    }

    private void findNextInCurrentTab() {
        Component comp = tabbedPane.getSelectedComponent();
        if (comp == null) {
            return;
        }
        if (!(comp instanceof JPanel)) {
            return;
        }
        JPanel p = (JPanel) comp;
        JScrollPane sp = (JScrollPane) p.getComponent(0);
        JViewport v = sp.getViewport();
        Component inner = v.getView();
        if (!(inner instanceof JTextPane)) {
            // in our implementation inner is the JScrollPane's viewport view - but we wrapped a panel
            // we search the JTextPane inside the viewport
            inner = findTextPaneInComponent(sp);
            if (inner == null) {
                return;
            }
        }
        JTextPane tp = (JTextPane) inner;
        String needle = searchField.getText();
        if (needle == null || needle.isEmpty()) {
            return;
        }
        String hay = null;
        try {
            hay = tp.getDocument().getText(0, tp.getDocument().getLength());
        } catch (BadLocationException ex) {
            return;
        }
        int selStart = tp.getSelectionStart();
        int pos = hay.indexOf(needle, selStart == tp.getSelectionEnd() ? selStart + 1 : selStart);
        if (pos < 0) {
            pos = hay.indexOf(needle);
        }
        if (pos >= 0) {
            tp.requestFocusInWindow();
            tp.select(pos, pos + needle.length());
            // scroll to selection
            try {
                Rectangle r = tp.modelToView(pos);
                if (r != null) {
                    r.y -= 20;
                }
                tp.scrollRectToVisible(r);
            } catch (BadLocationException ignored) {
            }
        } else {
            JOptionPane.showMessageDialog(this, "Text not found", "Find", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private JLabel createToolbarLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UI_FONT.deriveFont(Font.BOLD, (float) UI_FONT.getSize()));
        label.setForeground(TEXT_MUTED);
        return label;
    }

    private void stylePrimaryButton(AbstractButton button) {
        button.setUI(new BasicButtonUI());
        button.setRolloverEnabled(true);
        button.setFont(UI_FONT.deriveFont(Font.BOLD, (float) UI_FONT.getSize()));
        button.setForeground(Color.WHITE);
        button.setBackground(ACCENT_COLOR);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorder(new CompoundBorder(
                new LineBorder(ACCENT_DARK, 1, true),
                new EmptyBorder(scale(6), scale(18), scale(6), scale(18))
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setBorderPainted(true);

        ButtonModel model = button.getModel();
        model.addChangeListener(e -> {
            Color target;
            if (!model.isEnabled()) {
                target = BUTTON_DISABLED;
            } else if (model.isPressed()) {
                target = BUTTON_PRESSED;
            } else if (model.isRollover()) {
                target = BUTTON_HOVER;
            } else {
                target = ACCENT_COLOR;
            }
            button.setBackground(target);
            button.setBorder(new CompoundBorder(
                    new LineBorder(model.isRollover() ? BUTTON_HOVER.darker() : ACCENT_DARK, 1, true),
                    new EmptyBorder(scale(6), scale(18), scale(6), scale(18))
            ));
        });
    }

    private void styleTextField(JTextField field) {
        field.setFont(UI_FONT);
        field.setBackground(SURFACE_COLOR);
        field.setForeground(new Color(0x2c3139));
        field.setCaretColor(ACCENT_DARK);
        Border baseBorder = new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(scale(6), scale(10), scale(6), scale(10))
        );
        Border focusBorder = new CompoundBorder(
                new LineBorder(ACCENT_COLOR, 1, true),
                new EmptyBorder(scale(6), scale(10), scale(6), scale(10))
        );
        field.setBorder(baseBorder);
        Dimension pref = field.getPreferredSize();
        field.setPreferredSize(new Dimension(scale(pref.width), scale(pref.height)));
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(focusBorder);
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(baseBorder);
            }
        });
    }

    private static Font createUIFont() {
        return pickFont(
                new String[]{"Segoe UI", "Arial", "Tahoma", "Verdana", "SansSerif"},
                Font.PLAIN, scaledFontSize(20), Font.SANS_SERIF
        );
    }

    private static Font createMonoFont() {
        return pickFont(
                new String[]{"Consolas", "JetBrains Mono", "Fira Code", "DejaVu Sans Mono",
                    "Source Code Pro", "Courier New", "Lucida Console", Font.MONOSPACED},
                Font.PLAIN, scaledFontSize(BASE_FONT_SIZE), Font.MONOSPACED
        );
    }

    private static Font pickFont(String[] families, int style, int size, String fallbackFamily) {
        for (String family : families) {
            Font candidate = new Font(family, style, size);
            if (supportsVietnamese(candidate)) {
                return candidate;
            }
        }
        Font fallback = new Font(fallbackFamily, style, size);
        if (supportsVietnamese(fallback)) {
            return fallback;
        }
        return new Font(Font.SANS_SERIF, style, size);
    }

    private static boolean supportsVietnamese(Font font) {
        return font != null && font.canDisplayUpTo(VIET_SAMPLE_TEXT) == -1;
    }

    private static double determineUiScale() {
        try {
            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            int width = screen.width;
            if (width >= 3800) {
                return 1.75;
            }
            if (width >= 2560) {
                return 1.35;
            }
            if (width >= 1920) {
                return 1.15;
            }
        } catch (HeadlessException ignored) {
        }
        return 1.0;
    }

    private static int scale(int value) {
        return Math.max(1, (int) Math.round(value * UI_SCALE));
    }

    private static int scaledFontSize(int baseSize) {
        return Math.max(baseSize, scale(baseSize));
    }

    private static void applyUIFontScaling(double scaleFactor) {
        if (scaleFactor <= 1.0) {
            return;
        }
        updateFontDefaults(UIManager.getLookAndFeelDefaults(), scaleFactor);
        updateFontDefaults(UIManager.getDefaults(), scaleFactor);
    }

    private static void updateFontDefaults(UIDefaults defaults, double factor) {
        if (defaults == null) {
            return;
        }
        Enumeration<?> keys = defaults.keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = defaults.get(key);
            if (value instanceof Font) {
                Font font = (Font) value;
                Font scaled = font.deriveFont((float) (font.getSize2D() * factor));
                defaults.put(key, scaled);
            }
        }
    }

    private Component findTextPaneInComponent(Container c) {
        for (Component comp : c.getComponents()) {
            if (comp instanceof JTextPane) {
                return comp;
            }
            if (comp instanceof Container) {
                Component r = findTextPaneInComponent((Container) comp);
                if (r != null) {
                    return r;
                }
            }
        }
        return null;
    }

    private static String readFile(File file, Charset cs) throws IOException {
        byte[] bytes = Files.readAllBytes(file.toPath());
        return new String(bytes, cs);
    }

    // Small helper tree node wrapper to store File references in nodes
    private static class MethodInfo {

        final String name;
        final String returnType;
        final String accessModifier;
        final String modifiers;
        final String throwsClause;
        final List<ParamInfo> params;
        final int startOffset;
        final int endOffset;
        final int lineNumber;
        final String ownerType;
        final boolean constructor;
        final boolean inherited;

        MethodInfo(String name, String returnType, String accessModifier, String modifiers,
                String throwsClause, List<ParamInfo> params, int startOffset, int endOffset,
                int lineNumber, String ownerType, boolean constructor) {
            this(name, returnType, accessModifier, modifiers, throwsClause, params, startOffset,
                    endOffset, lineNumber, ownerType, constructor, false);
        }

        MethodInfo(String name, String returnType, String accessModifier, String modifiers,
                String throwsClause, List<ParamInfo> params, int startOffset, int endOffset,
                int lineNumber, String ownerType, boolean constructor, boolean inherited) {
            this.name = name;
            this.returnType = returnType != null ? returnType : "";
            this.accessModifier = accessModifier != null ? accessModifier.trim() : "";
            this.modifiers = modifiers != null ? modifiers.trim() : "";
            this.throwsClause = throwsClause != null ? throwsClause.trim() : "";
            this.params = params;
            this.startOffset = startOffset;
            this.endOffset = endOffset;
            this.lineNumber = lineNumber;
            this.ownerType = ownerType != null ? ownerType : "";
            this.constructor = constructor;
            this.inherited = inherited;
        }

        MethodInfo asInherited(String newOwner) {
            return new MethodInfo(name, returnType, accessModifier, modifiers, throwsClause,
                    params, startOffset, endOffset, lineNumber, newOwner != null ? newOwner : ownerType,
                    constructor, true);
        }

        String getReadableAccess() {
            if (accessModifier == null || accessModifier.isEmpty()) {
                return "mặc định (package-private)";
            }
            return accessModifier.trim();
        }

        String getElementKind() {
            if (constructor) {
                return "Hàm khởi tạo";
            }
            if ((returnType == null || returnType.isEmpty())
                    && (accessModifier == null || accessModifier.isEmpty())
                    && (modifiers == null || modifiers.isEmpty())) {
                return "Lời gọi API";
            }
            return "Phương thức";
        }

        boolean hasModifier(String keyword) {
            if (keyword == null || keyword.isEmpty() || modifiers == null) {
                return false;
            }
            String normalized = " " + modifiers.toLowerCase(Locale.ROOT) + " ";
            String probe = " " + keyword.toLowerCase(Locale.ROOT) + " ";
            return normalized.contains(probe);
        }
    }

    private static class ParamInfo {

        final String type;
        final String name;

        ParamInfo(String type, String name) {
            this.type = type;
            this.name = name;
        }
    }

    private static class WordMatch {

        final String word;
        final int start;
        final int end;

        WordMatch(String word, int start, int end) {
            this.word = word;
            this.start = start;
            this.end = end;
        }
    }

    private static class UnderlineHighlightPainter extends LayeredHighlighter.LayerPainter {

        private final Color color;

        UnderlineHighlightPainter(Color color) {
            this.color = color;
        }

        @Override
        public void paint(Graphics g, int offs0, int offs1, Shape bounds, JTextComponent c) {
            // Layered highlight handles painting via paintLayer.
        }

        @Override
        public Shape paintLayer(Graphics g, int offs0, int offs1, Shape bounds,
                                JTextComponent c, View view) {
            if (view == null) return null;
            Shape shape;
            try {
                shape = view.modelToView(offs0, Position.Bias.Forward, offs1, Position.Bias.Backward, bounds);
            } catch (BadLocationException ex) {
                return null;
            }
            if (shape == null) return null;
            Rectangle r = (shape instanceof Rectangle) ? (Rectangle) shape : shape.getBounds();
            g.setColor(color);
            int y = r.y + r.height - 1;
            g.drawLine(r.x, y, r.x + r.width, y);
            return r;
        }
    }

    private static class FileTreeNode {

        private final File file;

        public FileTreeNode(File file) {
            this.file = file;
        }

        public File getFile() {
            return file;
        }

        public String toString() {
            String name = file.getName();
            return name.isEmpty() ? file.getAbsolutePath() : name;
        }
    }

    public static void main(String[] args) {
        // Ensure using system look and feel for nicer UI
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        applyUIFontScaling(UI_SCALE);

        SwingUtilities.invokeLater(() -> {
            JavaSourceQuickView app = new JavaSourceQuickView();
            app.setVisible(true);
        });
    }
}
