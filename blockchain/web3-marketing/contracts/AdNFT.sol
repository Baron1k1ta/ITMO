// SPDX-License-Identifier: MIT
pragma solidity ^0.8.28;

import "@openzeppelin/contracts/token/ERC721/ERC721.sol";
import "@openzeppelin/contracts/access/AccessControl.sol";
import "@openzeppelin/contracts/utils/ReentrancyGuard.sol";

/// @title Web3 Marketing Ad NFT
/// @notice Демонстрация "cold advertising via NFT sending"
///         с опциями opt-out / блокировки рекламодателей.
contract Web3MarketingAdNFT is ERC721, AccessControl, ReentrancyGuard {
    // --- РОЛИ ---

    /// @dev Администратор (назначает рекламодателей).
    bytes32 public constant ADMIN_ROLE = DEFAULT_ADMIN_ROLE;

    /// @dev Роль рекламодателя, который имеет право создавать кампании и
    ///      рассылать рекламные NFT.
    bytes32 public constant ADVERTISER_ROLE = keccak256("ADVERTISER_ROLE");

    // --- СТРУКТУРЫ ДАННЫХ ---

    /// @dev Описание рекламной кампании.
    struct Campaign {
        uint256 id;            // идентификатор кампании
        address advertiser;    // владелец кампании (рекламодатель)
        string uri;            // базовый tokenURI для всех NFT данной кампании
        bool active;           // активна ли кампания
    }

    /// @dev id -> кампания
    mapping(uint256 => Campaign) public campaigns;

    /// @dev Текущее количество созданных кампаний (id последней кампании).
    uint256 public lastCampaignId;

    /// @dev NFT tokenId -> id кампании, из которой он был отправлен.
    mapping(uint256 => uint256) public campaignOfToken;

    /// @dev Глобальный opt-out: true = не хочу получать рекламу ни от кого.
    mapping(address => bool) public globalOptOut;

    /// @dev Блокировка конкретного рекламодателя:
    /// user => advertiser => isBlocked
    mapping(address => mapping(address => bool)) public blockedAdvertisers;

    /// @dev Счётчик токенов.
    uint256 private _nextTokenId = 1;

    // --- СОБЫТИЯ ---

    event CampaignCreated(
        uint256 indexed campaignId,
        address indexed advertiser,
        string uri
    );

    event CampaignStatusChanged(
        uint256 indexed campaignId,
        bool active
    );

    event AdSent(
        uint256 indexed campaignId,
        uint256 indexed tokenId,
        address indexed to
    );

    event GlobalOptOutChanged(
        address indexed user,
        bool status
    );

    event AdvertiserBlockChanged(
        address indexed user,
        address indexed advertiser,
        bool blocked
    );

    // --- КОНСТРУКТОР ---

    constructor() ERC721("Web3MarketingAdNFT", "ADNFT") {
        _grantRole(ADMIN_ROLE, msg.sender);
    }

    // --- МОДИФИКАТОРЫ ---

    modifier onlyAdmin() {
        require(hasRole(ADMIN_ROLE, msg.sender), "Not admin");
        _;
    }

    modifier onlyAdvertiser() {
        require(hasRole(ADVERTISER_ROLE, msg.sender), "Not advertiser");
        _;
    }

    // --- УПРАВЛЕНИЕ РОЛЯМИ ---

    /// @notice Назначить рекламодателя.
    function addAdvertiser(address advertiser) external onlyAdmin {
        require(advertiser != address(0), "Zero address");
        grantRole(ADVERTISER_ROLE, advertiser);
    }

    /// @notice Убрать роль рекламодателя.
    function removeAdvertiser(address advertiser) external onlyAdmin {
        revokeRole(ADVERTISER_ROLE, advertiser);
    }

    // --- ОПЦИИ ПОЛЬЗОВАТЕЛЯ (OPT-OUT / BLOCK) ---

    /// @notice Включить/выключить глобальный opt-out.
    /// @param status true = больше не получать рекламу ни от кого.
    function setGlobalOptOut(bool status) external {
        globalOptOut[msg.sender] = status;
        emit GlobalOptOutChanged(msg.sender, status);
    }

    /// @notice Заблокировать или разблокировать конкретного рекламодателя.
    /// @param advertiser адрес рекламодателя.
    /// @param blocked true = блокирую, false = снимаю блокировку.
    function setAdvertiserBlocked(address advertiser, bool blocked) external {
        require(advertiser != address(0), "Zero address");
        blockedAdvertisers[msg.sender][advertiser] = blocked;
        emit AdvertiserBlockChanged(msg.sender, advertiser, blocked);
    }

    // --- КАМПАНИИ ---

    /// @notice Создать новую рекламную кампанию.
    /// @param uri tokenURI, описывающий рекламный креатив (NFT метаданные).
    /// @return campaignId id созданной кампании.
    function createCampaign(string calldata uri)
        external
        onlyAdvertiser
        returns (uint256 campaignId)
    {
        require(bytes(uri).length > 0, "Empty URI");

        campaignId = ++lastCampaignId;
        campaigns[campaignId] = Campaign({
            id: campaignId,
            advertiser: msg.sender,
            uri: uri,
            active: true
        });

        emit CampaignCreated(campaignId, msg.sender, uri);
    }

    /// @notice Активировать/деактивировать кампанию.
    ///        Делать это может либо админ, либо сам рекламодатель.
    function setCampaignStatus(uint256 campaignId, bool active) external {
        Campaign storage c = campaigns[campaignId];
        require(c.id != 0, "Campaign not found");
        require(
            msg.sender == c.advertiser || hasRole(ADMIN_ROLE, msg.sender),
            "Not campaign owner or admin"
        );

        c.active = active;
        emit CampaignStatusChanged(campaignId, active);
    }

    // --- РАССЫЛКА РЕКЛАМНЫХ NFT ---

    /// @notice Холодная рассылка рекламных NFT по списку адресов.
    /// @dev Учитывает global opt-out и блокировку конкретного рекламодателя.
    /// @param campaignId id кампании.
    /// @param recipients массив адресов, куда попытаться отправить рекламу.
    function sendAdToMany(
        uint256 campaignId,
        address[] calldata recipients
    ) external onlyAdvertiser nonReentrant {
        Campaign memory c = campaigns[campaignId];
        require(c.id != 0, "Campaign not found");
        require(c.active, "Campaign not active");
        require(c.advertiser == msg.sender, "Not owner of campaign");
        require(recipients.length > 0, "Empty recipients");

        for (uint256 i = 0; i < recipients.length; i++) {
            address to = recipients[i];
            if (to == address(0)) {
                continue; // пропускаем нулевой адрес
            }

            // Учитываем opt-out
            if (globalOptOut[to]) {
                continue;
            }

            // Учитываем блокировку рекламодателя пользователем
            if (blockedAdvertisers[to][msg.sender]) {
                continue;
            }

            // Минтим NFT как "рекламный объект"
            uint256 tokenId = _nextTokenId++;
            _safeMint(to, tokenId);
            campaignOfToken[tokenId] = campaignId;

            emit AdSent(campaignId, tokenId, to);
        }
    }

    // --- МЕТАДАННЫЕ ---

    /// @notice Все NFT внутри одной кампании делят один tokenURI (один креатив).
    function tokenURI(uint256 tokenId)
        public
        view
        override
        returns (string memory)
    {
        _requireOwned(tokenId);
        uint256 campaignId = campaignOfToken[tokenId];
        Campaign memory c = campaigns[campaignId];
        return c.uri;
    }

    // --- ПОДДЕРЖКА ИНТЕРФЕЙСОВ / ТРАНСФЕРЫ ---

    /// @notice Soulbound: блокируем обычные трансферы, разрешаем только mint/burn.
    function _update(address to, uint256 tokenId, address auth)
        internal
        override
        returns (address)
    {
        address from = _ownerOf(tokenId);
        bool isMint = from == address(0);
        bool isBurn = to == address(0);
        if (!isMint && !isBurn) {
            revert("Transfers disabled");
        }
        return super._update(to, tokenId, auth);
    }

    function supportsInterface(bytes4 interfaceId)
        public
        view
        override(ERC721, AccessControl)
        returns (bool)
    {
        return super.supportsInterface(interfaceId);
    }

    // --- ВСПОМОГАТЕЛЬНЫЕ ФУНКЦИИ ---

    /// @notice Проверка, может ли указанный рекламодатель отправить рекламу
    ///         этому пользователю с учётом opt-out и блокировок.
    function canReceiveFromAdvertiser(
        address user,
        address advertiser
    ) external view returns (bool) {
        if (globalOptOut[user]) return false;
        if (blockedAdvertisers[user][advertiser]) return false;
        return true;
    }
}
