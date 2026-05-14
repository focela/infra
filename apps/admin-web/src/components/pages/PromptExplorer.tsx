import { useState, useCallback, useMemo } from 'react';

// material-ui
import { useColorScheme, useTheme } from '@mui/material/styles';
import Button from '@mui/material/Button';
import Collapse from '@mui/material/Collapse';
import Divider from '@mui/material/Divider';
import InputAdornment from '@mui/material/InputAdornment';
import InputBase from '@mui/material/InputBase';
import Stack from '@mui/material/Stack';
import Switch from '@mui/material/Switch';
import Typography from '@mui/material/Typography';
import Box from '@mui/material/Box';

// project imports
import MainCard from 'components/MainCard';
import { ThemeMode } from 'config';
import SyntaxHighlight from 'utils/SyntaxHighlight';
import { withAlpha } from 'utils/colorUtils';
import { promptsFileReader, agentsData, PromptFile, PromptFolder } from 'utils/promptsFileReader';

// assets
import CrownFilled from '@ant-design/icons/CrownFilled';
import DownOutlined from '@ant-design/icons/DownOutlined';
import FolderFilled from '@ant-design/icons/FolderFilled';
import FolderOpenFilled from '@ant-design/icons/FolderOpenFilled';
import FileMarkdownOutlined from '@ant-design/icons/FileMarkdownOutlined';
import RightOutlined from '@ant-design/icons/RightOutlined';
import SearchOutlined from '@ant-design/icons/SearchOutlined';
import LockFilled from '@ant-design/icons/LockFilled';

interface FolderItemProps {
  folder: PromptFolder;
  isExpanded: boolean;
  onToggle: () => void;
  selectedFile: { folderName: string; fileName: string } | null;
  onFileClick: (folder: PromptFolder, file: PromptFile) => void;
}

// ==============================|| DEFAULT WELCOME CONTENT ||============================== //

const defaultContent = `# 📂 Mantis Prompts Explorer
#
# Welcome to the Prompts Explorer!
#
# 👈 Click on a folder to expand it
# 📄 Then click on a file to preview its content
#
# 🟡 Files marked with a crown icon are Pro-only
# 🟢 All other files are free to use
#
# ────────────────────────────
#
# Available categories:
#   📁 api        - Backend integration prompts
#   📁 apps       - Application feature prompts
#   📁 auth       - Authentication prompts
#   📁 common     - Common utility prompts
#   📁 dashboard  - Dashboard enhancement prompts
#   📁 i18n       - Internationalization prompts
#   📁 landing    - Landing page prompts
#   📁 menu       - Menu customization prompts
#   📁 theming    - Theme & styling prompts
#   📁 ui-elements - UI component prompts`;

// ==============================|| FOLDER ITEM ||============================== //

function FolderItem({ folder, isExpanded, onToggle, selectedFile, onFileClick }: FolderItemProps) {
  const theme = useTheme();

  return (
    <>
      {/* Folder Header */}
      <Stack
        direction="row"
        spacing={0.5}
        onClick={onToggle}
        sx={{
          cursor: 'pointer',
          py: 0.35,
          px: 0.5,
          mx: -0.5,
          borderRadius: 0.5,
          transition: 'all 0.15s ease',
          '&:hover': {
            bgcolor: withAlpha(theme.vars.palette.grey[600], 0.2)
          },
          alignItems: 'center'
        }}
      >
        {isExpanded ? (
          <DownOutlined style={{ fontSize: 8, color: theme.vars.palette.grey[500] }} />
        ) : (
          <RightOutlined style={{ fontSize: 8, color: theme.vars.palette.grey[500] }} />
        )}
        {isExpanded ? (
          <FolderOpenFilled style={{ fontSize: 13, color: theme.vars.palette.primary.main }} />
        ) : (
          <FolderFilled style={{ fontSize: 13, color: theme.vars.palette.primary.main }} />
        )}
        <Typography variant="caption" sx={{ color: 'grey.300', fontSize: '0.7rem', userSelect: 'none' }}>
          {folder.name}
        </Typography>
        <Typography variant="caption" sx={{ color: 'grey.600', fontSize: '0.6rem', ml: 'auto !important' }}>
          {folder.files.length}
        </Typography>
      </Stack>

      {/* Files List */}
      <Collapse in={isExpanded} timeout={200}>
        <Stack sx={{ pl: 1.5, mt: 0.25 }}>
          {folder.files.map((file) => {
            const isSelected = selectedFile?.folderName === folder.name && selectedFile?.fileName === file.fileName;
            const isPro = file.type === 'pro';

            return (
              <Stack
                key={file.fileName}
                direction="row"
                spacing={0.75}
                onClick={() => onFileClick(folder, file)}
                sx={{
                  alignItems: 'center',
                  cursor: 'pointer',
                  py: 0.3,
                  px: 0.75,
                  mx: -0.75,
                  borderRadius: 0.5,
                  transition: 'all 0.15s ease',
                  ...(isSelected
                    ? {
                        bgcolor: withAlpha(theme.vars.palette.primary.main, 0.15),
                        borderLeft: `2px solid ${theme.vars.palette.primary.main}`
                      }
                    : {
                        borderLeft: '2px solid transparent',
                        '&:hover': {
                          bgcolor: withAlpha(theme.vars.palette.grey[600], 0.15)
                        }
                      })
                }}
              >
                <FileMarkdownOutlined
                  style={{
                    fontSize: 12,
                    color: isSelected ? theme.vars.palette.primary.main : theme.vars.palette.grey[500],
                    flexShrink: 0
                  }}
                />
                <Typography
                  variant="caption"
                  noWrap
                  sx={{
                    color: isSelected ? 'white' : 'grey.400',
                    fontWeight: isSelected ? 600 : 400,
                    fontSize: '0.65rem',
                    userSelect: 'none',
                    flex: 1,
                    minWidth: 0
                  }}
                >
                  {file.name}
                </Typography>
                {isPro && (
                  <CrownFilled
                    style={{
                      fontSize: 9,
                      color: theme.vars.palette.warning.main,
                      flexShrink: 0
                    }}
                  />
                )}
              </Stack>
            );
          })}
        </Stack>
      </Collapse>
    </>
  );
}

// ==============================|| UPGRADE PRO OVERLAY ||============================== //

function UpgradeProOverlay() {
  const theme = useTheme();

  // Upgrade section sits below the code preview - no absolute positioning
  return (
    <Box
      sx={{
        flexShrink: 0,
        display: 'flex',
        flexDirection: 'column',
        zIndex: 2
      }}
    >
      {/* Gradient fade overlay */}
      <Box
        sx={{
          height: 60,
          background: `linear-gradient(to bottom, transparent, ${theme.vars.palette.grey[900]})`,
          flexShrink: 0,
          mt: -7.5,
          ...theme.applyStyles('dark', { background: `linear-gradient(to bottom, transparent, ${theme.vars.palette.grey[100]})` })
        }}
      />
      {/* Upgrade section */}
      <Stack
        direction="column"
        sx={{
          alignItems: 'center',
          bgcolor: theme.vars.palette.grey[900],
          p: 2,
          ...theme.applyStyles('dark', { bgcolor: theme.vars.palette.secondary[100] })
        }}
      >
        <Stack
          sx={{
            width: 36,
            height: 36,
            borderRadius: '50%',
            bgcolor: withAlpha(theme.vars.palette.warning.main, 0.15),
            alignItems: 'center',
            justifyContent: 'center',
            mb: 1,
            ...theme.applyStyles('dark', { bgcolor: withAlpha(theme.vars.palette.warning.main, 0.15) })
          }}
        >
          <LockFilled style={{ fontSize: 16, color: theme.vars.palette.warning.main }} />
        </Stack>
        <Typography variant="caption" sx={{ color: 'grey.300', fontWeight: 600, mb: 0.5, fontSize: '0.75rem' }}>
          Upgrade to Pro to view full prompt
        </Typography>
        <Typography variant="caption" sx={{ color: 'grey.500', display: 'block', mb: 1.5, fontSize: '0.65rem', textAlign: 'center' }}>
          You're viewing a preview. Unlock all premium prompts with Pro.
        </Typography>
        <Button
          variant="contained"
          size="small"
          sx={{
            bgcolor: theme.vars.palette.warning.main,
            color: 'grey.900',
            fontWeight: 700,
            fontSize: '0.7rem',
            px: 2.5,
            py: 0.75,
            borderRadius: 1,
            textTransform: 'none',
            '&:hover': {
              bgcolor: theme.vars.palette.warning.dark
            }
          }}
        >
          <CrownFilled style={{ fontSize: 12, marginRight: 6 }} />
          Upgrade to Pro
        </Button>
      </Stack>
    </Box>
  );
}

// ==============================|| CODE EDITOR CARD ||============================== //

export default function PromptExplorer() {
  const theme = useTheme();
  const { colorScheme } = useColorScheme();

  const [isPromptsOpen, setIsPromptsOpen] = useState(true);
  const [expandedFolders, setExpandedFolders] = useState<string[]>([]);
  const [selectedFileId, setSelectedFileId] = useState<{
    folderName: string;
    fileName: string;
  } | null>(null);
  const [showProOnly, setShowProOnly] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');

  // Filter prompts based on toggle (free/pro) and search query
  const filteredPrompts = useMemo(() => {
    const query = searchQuery.toLowerCase().trim();
    const typeFilter: 'pro' | 'free' = showProOnly ? 'pro' : 'free';

    return promptsFileReader
      .map((folder) => {
        const matchedFiles = folder.files.filter((file) => {
          const matchesType = file.type === typeFilter;
          const matchesSearch =
            !query ||
            file.name.toLowerCase().includes(query) ||
            file.fileName.toLowerCase().includes(query) ||
            folder.name.toLowerCase().includes(query);
          return matchesType && matchesSearch;
        });
        return { ...folder, files: matchedFiles };
      })
      .filter((folder) => folder.files.length > 0);
  }, [showProOnly, searchQuery]);

  const handleToggleFolder = useCallback((folderName: string) => {
    setExpandedFolders((prev) => (prev.includes(folderName) ? prev.filter((f) => f !== folderName) : [...prev, folderName]));
  }, []);

  const handleFileClick = useCallback((folderName: string, file: PromptFile) => {
    setSelectedFileId({
      folderName,
      fileName: file.fileName
    });
  }, []);

  // Find the selected file from the latest data
  const selectedFile = useMemo(() => {
    if (!selectedFileId) return null;
    if (selectedFileId.folderName === 'root' && selectedFileId.fileName === 'AGENTS.md') {
      return agentsData;
    }
    return promptsFileReader.find((f) => f.name === selectedFileId.folderName)?.files.find((f) => f.fileName === selectedFileId.fileName);
  }, [selectedFileId]);

  // Determine what to show in the title bar
  const titleBarText =
    selectedFileId && selectedFile
      ? `${selectedFileId.folderName === 'root' ? '' : selectedFileId.folderName + ' — '}${selectedFile.name}`
      : 'Prompts Explorer';

  // Determine what content to show
  const isPro = selectedFile?.type === 'pro';
  const isAgents = selectedFileId?.fileName === 'AGENTS.md';
  const displayContent = useMemo(() => {
    if (!selectedFile) return defaultContent;
    if (isAgents) {
      // Get first 5 lines for AGENTS.md
      return selectedFile.content.split('\n').slice(0, 15).join('\n') + '\n\n... (full 300+ line instructions hidden)';
    }
    if (isPro) {
      // Show first 15 lines of pro prompts as a preview
      const lines = selectedFile.content.split('\n');
      return lines.slice(0, 15).join('\n');
    }
    return selectedFile.content;
  }, [selectedFile, isAgents, isPro]);

  return (
    <MainCard
      content={false}
      sx={{
        bgcolor: withAlpha('grey.700', 0.98),
        width: 1,
        borderColor: 'grey.600',
        backdropFilter: 'blur(20px)',
        borderRadius: 2,
        overflow: 'hidden',
        p: 0,
        '& .MuiCardContent-root': {
          p: 0
        },
        ...theme.applyStyles('dark', {
          bgcolor: theme.vars.palette.secondary[100],
          borderColor: withAlpha(theme.vars.palette.secondary.darker, 0.05)
        })
      }}
    >
      {/* Window Title Bar */}
      <Stack
        direction="row"
        sx={{
          gap: 0.75,
          alignItems: 'center',
          px: 2,
          py: 1.5,
          bgcolor: withAlpha('grey.800', 0.6),
          borderBottom: `1px solid ${withAlpha(theme.vars.palette.grey[600], 0.2)}`,
          ...theme.applyStyles('dark', {
            bgcolor: withAlpha(theme.vars.palette.secondary.darker, 0.05),
            borderColor: withAlpha(theme.vars.palette.secondary.darker, 0.05)
          })
        }}
      >
        <Box sx={{ width: 12, height: 12, borderRadius: '50%', bgcolor: theme.vars.palette.error.main }} />
        <Box sx={{ width: 12, height: 12, borderRadius: '50%', bgcolor: theme.vars.palette.warning.main }} />
        <Box sx={{ width: 12, height: 12, borderRadius: '50%', bgcolor: theme.vars.palette.success.main }} />
        <Typography variant="caption" sx={{ color: 'grey.400', pl: 1.5, flexGrow: 1, textAlign: 'start', pr: 6 }} noWrap>
          {titleBarText}
        </Typography>
      </Stack>

      {/* Main Content Area with Sidebar */}
      <Stack direction="row" sx={{ height: { xs: 1, sm: 440 }, display: { xs: 'block', sm: 'flex' } }}>
        {/* Sidebar - File Explorer */}
        <Box
          sx={{
            width: { xs: 1, sm: 150, lg: 200 },
            bgcolor: withAlpha('grey.800', 0.98),
            borderRight: `1px solid ${withAlpha(theme.vars.palette.grey[700], 0.2)}`,
            p: 1.5,
            overflow: 'auto',
            '&::-webkit-scrollbar': {
              width: 5,
              height: 5
            },
            '&::-webkit-scrollbar-track': {
              bgcolor: 'transparent'
            },
            '&::-webkit-scrollbar-thumb': {
              bgcolor: withAlpha(theme.vars.palette.grey[600], 0.3),
              borderRadius: 2
            },
            '&::-webkit-scrollbar-thumb:hover': {
              bgcolor: withAlpha(theme.vars.palette.grey[600], 0.5)
            },
            ...theme.applyStyles('dark', {
              bgcolor: withAlpha(theme.vars.palette.secondary.darker, 0.05),
              borderColor: withAlpha(theme.vars.palette.secondary.darker, 0.05)
            })
          }}
        >
          <Stack direction="row" sx={{ alignItems: 'center', mb: 1 }}>
            <Typography variant="caption" sx={{ color: 'grey.500', fontWeight: 600, letterSpacing: 0.5, fontSize: '0.65rem' }}>
              EXPLORER
            </Typography>
            <Stack direction="row" sx={{ ml: 'auto', alignItems: 'center', gap: 0 }}>
              <Typography
                variant="caption"
                sx={{ color: !showProOnly ? 'success.main' : 'grey.600', fontSize: '0.55rem', fontWeight: 600 }}
              >
                Free
              </Typography>
              <Switch
                size="small"
                checked={showProOnly}
                onChange={(e) => setShowProOnly(e.target.checked)}
                sx={{
                  width: 28,
                  height: 16,
                  p: 0,
                  '& .MuiSwitch-switchBase': {
                    p: '2px',
                    '&.Mui-checked': {
                      transform: 'translateX(12px)',
                      color: 'warning.main',
                      '& + .MuiSwitch-track': {
                        bgcolor: withAlpha(theme.vars.palette.warning.main, 0.4),
                        opacity: 1
                      }
                    }
                  },
                  '& .MuiSwitch-thumb': {
                    width: 12,
                    height: 12
                  },
                  '& .MuiSwitch-track': {
                    borderRadius: 8,
                    bgcolor: withAlpha(theme.vars.palette.success.main, 0.4),
                    opacity: 1
                  }
                }}
              />
              <Typography variant="caption" sx={{ color: showProOnly ? 'warning.main' : 'grey.600', fontSize: '0.55rem', fontWeight: 600 }}>
                Pro
              </Typography>
            </Stack>
          </Stack>

          {/* Search Input */}
          <InputBase
            placeholder="Search prompts..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            size="small"
            startAdornment={
              <InputAdornment position="start">
                <SearchOutlined style={{ fontSize: 12, color: theme.vars.palette.grey[500] }} />
              </InputAdornment>
            }
            sx={{
              mb: 1,
              px: 1,
              py: 0.25,
              bgcolor: withAlpha(theme.vars.palette.grey[600], 0.15),
              borderRadius: 0.75,
              border: `1px solid ${withAlpha(theme.vars.palette.grey[600], 0.2)}`,
              fontSize: '0.65rem',
              color: 'grey.300',
              width: 1,
              '& input': {
                p: 0,
                fontSize: '0.65rem',
                '&::placeholder': {
                  color: theme.vars.palette.grey[600],
                  opacity: 1
                }
              },
              ...theme.applyStyles('dark', {
                bgcolor: withAlpha(theme.vars.palette.secondary.darker, 0.08),
                borderColor: withAlpha(theme.vars.palette.secondary.darker, 0.1)
              })
            }}
          />

          <Stack spacing={0.5}>
            {/* AGENTS.md File */}
            {agentsData && (
              <Stack
                direction="row"
                spacing={0.75}
                onClick={() => handleFileClick('root', agentsData!)}
                sx={{
                  alignItems: 'center',
                  cursor: 'pointer',
                  py: 0.35,
                  px: 0.75,
                  mx: -0.75,
                  borderRadius: 0.5,
                  transition: 'all 0.15s ease',
                  ...(selectedFileId?.fileName === 'AGENTS.md'
                    ? {
                        bgcolor: withAlpha(theme.vars.palette.primary.main, 0.15),
                        borderLeft: `2px solid ${theme.vars.palette.primary.main}`
                      }
                    : {
                        borderLeft: '2px solid transparent',
                        '&:hover': {
                          bgcolor: withAlpha(theme.vars.palette.grey[600], 0.15)
                        }
                      })
                }}
              >
                <FileMarkdownOutlined
                  style={{
                    fontSize: 12,
                    color: selectedFileId?.fileName === 'AGENTS.md' ? theme.vars.palette.primary.main : theme.vars.palette.grey[500],
                    flexShrink: 0
                  }}
                />
                <Typography
                  variant="caption"
                  noWrap
                  sx={{
                    color: selectedFileId?.fileName === 'AGENTS.md' ? 'white' : 'grey.400',
                    fontWeight: selectedFileId?.fileName === 'AGENTS.md' ? 600 : 400,
                    fontSize: '0.7rem',
                    userSelect: 'none',
                    flex: 1
                  }}
                >
                  AGENTS.md
                </Typography>
                <CrownFilled style={{ fontSize: 10, color: theme.vars.palette.warning.main, flexShrink: 0 }} />
              </Stack>
            )}

            {/* Prompts root folder */}
            <Stack
              direction="row"
              spacing={0.5}
              onClick={() => setIsPromptsOpen(!isPromptsOpen)}
              sx={{
                cursor: 'pointer',
                py: 0.35,
                px: 0.5,
                mx: -0.5,
                mt: 1,
                borderRadius: 0.5,
                transition: 'all 0.15s ease',
                '&:hover': {
                  bgcolor: withAlpha(theme.vars.palette.grey[600], 0.2)
                },
                alignItems: 'center'
              }}
            >
              {isPromptsOpen ? (
                <DownOutlined style={{ fontSize: 8, color: theme.vars.palette.grey[500] }} />
              ) : (
                <RightOutlined style={{ fontSize: 8, color: theme.vars.palette.grey[500] }} />
              )}
              {isPromptsOpen ? (
                <FolderOpenFilled style={{ fontSize: 13, color: theme.vars.palette.primary.main }} />
              ) : (
                <FolderFilled style={{ fontSize: 13, color: theme.vars.palette.primary.main }} />
              )}
              <Typography variant="caption" sx={{ color: 'primary.main', fontWeight: 700, fontSize: '0.7rem', userSelect: 'none' }}>
                prompts
              </Typography>
              <Typography variant="caption" sx={{ color: 'grey.600', fontSize: '0.6rem', ml: 'auto !important' }}>
                {filteredPrompts.reduce((acc, f) => acc + f.files.length, 0)}
              </Typography>
            </Stack>

            {/* Dynamic subfolder list */}
            <Collapse in={isPromptsOpen} timeout={250}>
              <Stack sx={{ pl: 1, mt: 0.25 }} spacing={0.25}>
                {filteredPrompts.map((folder) => (
                  <FolderItem
                    key={folder.name}
                    folder={folder}
                    isExpanded={expandedFolders.includes(folder.name)}
                    onToggle={() => handleToggleFolder(folder.name)}
                    selectedFile={selectedFileId}
                    onFileClick={(f, file) => handleFileClick(f.name, file)}
                  />
                ))}
              </Stack>
            </Collapse>
          </Stack>
        </Box>

        {/* Code Editor Area */}
        <Box
          sx={{
            flex: 1,
            display: 'flex',
            flexDirection: 'column',
            bgcolor: withAlpha('grey.900', 0.5),
            overflow: 'hidden',
            '& pre': {
              margin: 0,
              background: 'transparent !important',
              padding: '0 !important'
            },
            '& code': {
              fontSize: '0.7rem !important',
              lineHeight: '1.5 !important'
            },
            ...theme.applyStyles('dark', { bgcolor: withAlpha(theme.vars.palette.grey[100], 0.98) })
          }}
        >
          {/* Scrollable content area */}
          <Box
            sx={{
              flex: 1,
              minHeight: 0,
              p: 2,
              ...(isPro
                ? { overflow: 'hidden' }
                : {
                    overflowX: 'hidden',
                    overflowY: 'auto',
                    '&::-webkit-scrollbar': {
                      width: 5,
                      height: 5
                    },
                    '&::-webkit-scrollbar-track': {
                      bgcolor: 'transparent'
                    },
                    '&::-webkit-scrollbar-thumb': {
                      bgcolor: withAlpha(theme.vars.palette.grey[600], 0.3),
                      borderRadius: 2
                    },
                    '&::-webkit-scrollbar-thumb:hover': {
                      bgcolor: withAlpha(theme.vars.palette.grey[600], 0.5)
                    }
                  }),
              '&& pre': { backgroundColor: `${theme.vars.palette.grey[colorScheme === ThemeMode.DARK ? 100 : 900]} !important` }
            }}
          >
            {/* Description Block */}
            {selectedFile?.description && (
              <>
                <Box
                  sx={{
                    bgcolor: withAlpha(theme.vars.palette.primary.main, 0.08),
                    border: `1px solid ${withAlpha(theme.vars.palette.primary.main, 0.2)}`,
                    borderRadius: 1,
                    px: 1.5,
                    py: 1,
                    mb: 1.5,
                    ...theme.applyStyles('dark', { bgcolor: withAlpha(theme.vars.palette.primary.light, 0.9) })
                  }}
                >
                  <Typography
                    variant="caption"
                    sx={{
                      color: 'primary.light',
                      fontWeight: 600,
                      fontSize: '0.65rem',
                      letterSpacing: 0.5,
                      textTransform: 'uppercase',
                      display: 'block',
                      mb: 0.5,
                      ...theme.applyStyles('dark', { color: theme.vars.palette.secondary.main })
                    }}
                  >
                    Description
                  </Typography>
                  <Typography
                    variant="body2"
                    sx={{
                      color: 'grey.300',
                      fontSize: '0.7rem',
                      lineHeight: 1.6,
                      display: '-webkit-box',
                      WebkitLineClamp: 5,
                      WebkitBoxOrient: 'vertical',
                      overflow: 'hidden',
                      fontStyle: 'italic',
                      ...theme.applyStyles('dark', { color: theme.vars.palette.secondary.main })
                    }}
                  >
                    {selectedFile.description}
                  </Typography>
                </Box>
                <Divider sx={{ borderColor: withAlpha(theme.vars.palette.grey[600], 0.2), mb: 1.5 }} />
              </>
            )}

            {/* Code Content */}
            <SyntaxHighlight language="markdown" showLineNumbers={true} customStyle={{ margin: 0, padding: 0 }} darkStyle>
              {displayContent}
            </SyntaxHighlight>
          </Box>

          {/* Pro Overlay - sits below content, no scrollbar leaks */}
          {isPro && <UpgradeProOverlay />}
        </Box>
      </Stack>
    </MainCard>
  );
}
