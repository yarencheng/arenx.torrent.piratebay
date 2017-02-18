package arenx.torrent.piratebay;

public enum Category {
	ALL(0),
	AllAudio(100),
	AllVideo(200),
	AllApplication(300),
	AllGames(400),
	AllPorn(500),
	AllOther(600),
	
	Music(101),
	Audiobooks(102),
	Soundclips(103),
	FLAC(104),
	OtherAudio(199),

	Movies(201),
	MoviesDVDR(202),
	Musicvideos(203),
	Movieclips(204),
	TVshows(205),
	HandheldVideo(206),
	HDMovies(207),
	HDTVshows(208),
	Movies3D(209),
	OtherVideo(299),

	WindowsApplications(301),
	MacApplications(302),
	UNIXApplications(303),
	HandheldApplications(304),
	IOSApplications(305),
	AndroidApplications(306),
	OtherOSApplications(399),

	PCGames(401),
	MacGames(402),
	PSxGames(403),
	XBOX360Games(404),
	WiiGames(405),
	HandheldGames(406),
	IOSGames(407),
	AndroidGames(408),
	OtherGames(499),

	MoviesPorn(501),
	MoviesDVDRPorn(502),
	PicturesPorn(503),
	GamesPorn(504),
	HDMoviesPorn(505),
	MovieclipsPorn(506),
	OtherPorn(599),

	Ebooks(601),
	Comics(602),
	Pictures(603),
	Covers(604),
	Physibles(605),
	OtherOther(699);
	
	public int number;
	
	private Category(int n){
		number = n;
	}
}
