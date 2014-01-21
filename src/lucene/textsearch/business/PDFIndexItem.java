package lucene.textsearch.business;

public class PDFIndexItem {
        private Long id;
    private String title;
    private String content;
    private String ncontent;

    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
        public static final String NCONTENT = "ncontent";

    public PDFIndexItem(Long id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "IndexItem{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

	public String getNcontent() {
		return ncontent;
	}

	public void setNcontent(String ncontent) {
		this.ncontent = ncontent;
	}

 


}