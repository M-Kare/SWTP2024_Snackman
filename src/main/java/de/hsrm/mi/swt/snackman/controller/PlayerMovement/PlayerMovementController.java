/*
todo change for ghost

    @MessageMapping("/topic/player/ghost/update")
    public void spreadPlayerGhostUpdate(GhostFrontendDTO player){
      Ghost ghost = mapService.getGhost(player.id());
      ghost.setQuaternion(player.qX(), player.qY(), player.qZ(), player.qW());
      ghost.move(player.forward(), player.backward(), player.left(), player.right(), player.delta());

      System.out.println("Ghost message : " + GhostDTO.fromGhost(ghost).toString());
      messagingTemplate.convertAndSend("/topic/player/ghost" , GhostDTO.fromGhost(ghost));

    }

 */