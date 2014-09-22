package org.csstudio.mps.sns.tools.data;

import java.util.*;

import org.csstudio.mps.sns.tools.data.Device;

public class MPSChassis extends Device 
{
  private ArrayList boards = new ArrayList();
  
  public MPSChassis()
  {
  }

  public MPSChassis(String deviceID)
  {
    super(deviceID);
  }

  public void addBoard(MPSBoard board)
  {
    boards.add(board);
    board.setChassis(this);
  }

  public int getBoardCount()
  {
    return boards.size();
  }

  public MPSBoard getBoardAt(int boardIndex)
  {
    return (MPSBoard)boards.get(boardIndex);
  }

  public void clear()
  {
    boards.clear();
  }
}