export type Block = {
    componentCode: string;
    componentDescription: string;
    locationCode: string;
    roomCode: string;
    buildingCode: string;
    room: string;
    section: string;
    classNumber: number;
    classAssociation: number;
    instructionModeCode: string;
    instructionModeDescription: string;
    meetingPatternNumber: string;
    modays: boolean;
    tuesdays: boolean;
    wednesdays: boolean;
    thursdays: boolean;
    fridays: boolean;
    saturdays: boolean;
    sundays: boolean;
    classStartTime: string;
    classEndTime: string;
    classStartDate: string;
    classEndDate: string;
};


export type Schedule = {
    blocks: Block[];
    term: string;
};
