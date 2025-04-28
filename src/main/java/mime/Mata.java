package mime;

import lombok.Data;

/**
 * @author charlottexiao
 */
@Data
public class Mata {

    /**
     * 音乐名
     */
    public String musicName;

    /**
     * 艺术家
     */
    public String[][] artist;

    /**
     * 专辑
     */
    public String album;

    /**
     * 格式
     */
    public String format;
}
