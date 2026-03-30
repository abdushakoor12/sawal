import 'package:shared_preferences/shared_preferences.dart';
import 'package:sqflite/sqflite.dart';
import 'package:path/path.dart' as p;
import '../models/chat_session.dart';
import '../models/message.dart';

class DatabaseService {
  static Database? _db;
  static SharedPreferences? _prefs;

  static Future<void> initialize() async {
    _prefs = await SharedPreferences.getInstance();
    final dbPath = await getDatabasesPath();
    const dbVersion = 1;
    final path = p.join(dbPath, 'sawal_chat.db');
    _db = await openDatabase(
      path,
      version: dbVersion,
      onCreate: (db, version) async {
        await db.execute('''
          CREATE TABLE sessions(
            id TEXT PRIMARY KEY,
            title TEXT NOT NULL,
            created_at TEXT NOT NULL,
            updated_at TEXT NOT NULL
          )
        ''');
        await db.execute('''
          CREATE TABLE messages(
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            session_id TEXT NOT NULL,
            text TEXT NOT NULL,
            is_user INTEGER NOT NULL,
            timestamp TEXT NOT NULL,
            FOREIGN KEY (session_id) REFERENCES sessions(id) ON DELETE CASCADE
          )
        ''');
        await db.execute('CREATE INDEX idx_messages_session ON messages(session_id)');
      },
    );
  }

  static Future<List<ChatSession>> getSessions() async {
    final sessionRows = await _db!.query('sessions', orderBy: 'updated_at DESC');
    final sessions = <ChatSession>[];
    
    for (final row in sessionRows) {
      final messageRows = await _db!.query(
        'messages',
        where: 'session_id = ?',
        whereArgs: [row['id']],
        orderBy: 'timestamp ASC',
      );
      
      final messages = messageRows.map((m) => Message(
        text: m['text'] as String,
        isUser: (m['is_user'] as int) == 1,
        timestamp: DateTime.parse(m['timestamp'] as String),
      )).toList();
      
      sessions.add(ChatSession(
        id: row['id'] as String,
        title: row['title'] as String,
        messages: messages,
        createdAt: DateTime.parse(row['created_at'] as String),
        updatedAt: DateTime.parse(row['updated_at'] as String),
      ));
    }
    
    return sessions;
  }

  static Future<ChatSession?> getSession(String id) async {
    final sessionRows = await _db!.query('sessions', where: 'id = ?', whereArgs: [id]);
    if (sessionRows.isEmpty) return null;
    
    final row = sessionRows.first;
    final messageRows = await _db!.query(
      'messages',
      where: 'session_id = ?',
      whereArgs: [id],
      orderBy: 'timestamp ASC',
    );
    
    final messages = messageRows.map((m) => Message(
      text: m['text'] as String,
      isUser: (m['is_user'] as int) == 1,
      timestamp: DateTime.parse(m['timestamp'] as String),
    )).toList();
    
    return ChatSession(
      id: row['id'] as String,
      title: row['title'] as String,
      messages: messages,
      createdAt: DateTime.parse(row['created_at'] as String),
      updatedAt: DateTime.parse(row['updated_at'] as String),
    );
  }

  static Future<void> saveSession(ChatSession session) async {
    await _db!.transaction((txn) async {
      await txn.insert(
        'sessions',
        {
          'id': session.id,
          'title': session.title,
          'created_at': session.createdAt.toIso8601String(),
          'updated_at': session.updatedAt.toIso8601String(),
        },
        conflictAlgorithm: ConflictAlgorithm.replace,
      );
      
      await txn.delete('messages', where: 'session_id = ?', whereArgs: [session.id]);
      
      for (final msg in session.messages) {
        await txn.insert('messages', {
          'session_id': session.id,
          'text': msg.text,
          'is_user': msg.isUser ? 1 : 0,
          'timestamp': msg.timestamp.toIso8601String(),
        });
      }
    });
  }

  static Future<void> deleteSession(String id) async {
    await _db!.delete('messages', where: 'session_id = ?', whereArgs: [id]);
    await _db!.delete('sessions', where: 'id = ?', whereArgs: [id]);
  }

  static Future<String?> getCurrentSessionId() async => _prefs!.getString('current_session_id');

  static Future<void> setCurrentSessionId(String? id) async {
    if (id == null) {
      await _prefs!.remove('current_session_id');
    } else {
      await _prefs!.setString('current_session_id', id);
    }
  }

  static Future<String?> getApiKey() async => _prefs!.getString('openrouter_api_key');
  static Future<void> setApiKey(String? key) async {
    if (key == null) {
      await _prefs!.remove('openrouter_api_key');
    } else {
      await _prefs!.setString('openrouter_api_key', key);
    }
  }

  static Future<String?> getSelectedModel() async => _prefs!.getString('selected_model');
  static Future<void> setSelectedModel(String? model) async {
    if (model == null) {
      await _prefs!.remove('selected_model');
    } else {
      await _prefs!.setString('selected_model', model);
    }
  }

  static Future<String?> getThemeMode() async => _prefs!.getString('theme_mode');
  static Future<void> setThemeMode(String? mode) async {
    if (mode == null) {
      await _prefs!.remove('theme_mode');
    } else {
      await _prefs!.setString('theme_mode', mode);
    }
  }
}