from . import ChickenMovementService

class ChickenMovement(ChickenMovementService):
    def __init__(self):
        self.value="Hello from python"
       
    def getHello(self):
        return self.value