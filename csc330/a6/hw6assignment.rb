# Programming Languages, Homework 6, hw6runner.rb

# This is the only file you turn in,
# so do not modify the other files as
# part of your solution.

class MyPiece < Piece
  # The constant All_My_Pieces should be declared here:

  All_My_Pieces = [
    [
      [[0, 0], [-1, 0], [-2, 0], [1, 0], [2, 0]],
      [[0, 0], [0, -1], [0, -2], [0, 1], [0, 2]]
    ],
    rotations([[0, 0], [1, 0], [0, 1]]),
    rotations([[0, 0], [0, 1], [1, 0], [1, 1], [-1, 0]]),
    [
      [[0, 0], [1, 0], [0, 1], [1, 1]]
    ],  # square (only needs one)
    rotations([[0, 0], [-1, 0], [1, 0], [0, -1]]), # T
    [
      [[0, 0], [-1, 0], [1, 0], [2, 0]], # long (only needs two)
      [[0, 0], [0, -1], [0, 1], [0, 2]]
    ],
    rotations([[0, 0], [0, -1], [0, 1], [1, 1]]), # L
    rotations([[0, 0], [0, -1], [0, 1], [-1, 1]]), # inverted L
    rotations([[0, 0], [-1, 0], [0, -1], [1, -1]]), # S
    rotations([[0, 0], [1, 0], [0, -1], [-1, -1]])
  ]

  # Your Enhancements here
  def self.next_piece (board)
    self.new(All_My_Pieces.sample, board)
  end

  def self.cheat (board)
    self.new([[0,0]], board)
  end

end

class MyBoard < Board
  # Your Enhancements here:

  def initialize (game)
    @grid = Array.new(num_rows) {Array.new(num_columns)}
    @current_block = MyPiece.next_piece(self)
    @score = 0
    @game = game
    @delay = 500
  end

  def next_piece
    if @cheat
      @current_block = MyPiece.cheat(self)
      @current_pos = nil
      @cheat = false
    else
      @current_block = MyPiece.next_piece(self)
      @current_pos = nil
    end
  end

  def current_block
    @current_block
  end

  def store_current
    locations = @current_block.current_rotation
    displacement = @current_block.position
    (0..locations.length-1).each{|index| 
      current = locations[index];
      @grid[current[1]+displacement[1]][current[0]+displacement[0]] = 
      @current_pos[index]
    }
    remove_filled
    @delay = [@delay - 2, 80].max
  end

  def rotate_180
    if !game_over? and @game.is_running?
      @current_block.move(0, 0, 2)
    end
    draw
  end

  def cheat
    if @score >= 100 and !@cheat
      @cheat = true
      @score -= 100
    end
  end

end

class MyTetris < Tetris
  # Your Enhancements here:

  def initialize
    super
    @root.bind('u', proc {@board.rotate_180})
    @root.bind('c', proc {@board.cheat})
  end

  def set_board
    @canvas = TetrisCanvas.new
    @board = MyBoard.new(self)
    @canvas.place(@board.block_size * @board.num_rows + 3,
                  @board.block_size * @board.num_columns + 6, 24, 80)
    @board.draw
  end
end

class MyPieceChallenge < MyPiece

  def all_rotations
    @all_rotations
  end

  def reset_pos
    @base_position = [5, 0]
  end

end

class MyBoardChallenge < MyBoard

  def initialize (game)
    super
    @current_block = MyPieceChallenge.next_piece(self)
  end

  def next_piece
    if @cheat
      @current_block = MyPieceChallenge.cheat(self)
      @current_pos = nil
      @cheat = false
    else
      @current_block = MyPieceChallenge.next_piece(self)
      @current_pos = nil
    end
  end

  def swap (piece)
    @current_block = piece
    @current_pos = nil
  end

  def remove_current_piece
    @current_pos.each{|b| b.remove}
  end

end

class MyTetrisChallenge < MyTetris

  def initialize
    super
    @root.bind('s', proc {self.hold(@board.current_block)})
    @held_box = TetrisCanvas.new
    @held_box.place(80, 80, 180, 50)
  end

  def set_board
    @canvas = TetrisCanvas.new
    @board = MyBoardChallenge.new(self)
    @canvas.place(@board.block_size * @board.num_rows + 3,
                  @board.block_size * @board.num_columns + 6, 24, 80)
    @board.draw
  end

  def hold (piece)
    @board.remove_current_piece
    if @trect
      @trect.each{|b| b.remove}
    end
    tmp = @held
    @held = piece
    size = @board.block_size
    blocks = piece.all_rotations[0]
    @trect = blocks.map{|block|
    TetrisRect.new(@held_box, 30 + block[0]*size,
                       30 + block[1]*size,
                       30 + size + block[0]*size, 
                       30 + size + block[1]*size, 
                       piece.color)}
    if !tmp
      @board.next_piece
    else
      tmp.reset_pos
      @board.swap(tmp)
    end
  end
end
