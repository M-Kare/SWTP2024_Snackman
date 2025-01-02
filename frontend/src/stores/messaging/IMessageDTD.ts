export interface IMessageDTD{
    event: EventType,
    message: any
}

export enum EventType{
    MobUpdate = "MobUpdate", SquareUpdate = "SquareUpdate", ChickenUpdate = "ChickenUpdate"
}